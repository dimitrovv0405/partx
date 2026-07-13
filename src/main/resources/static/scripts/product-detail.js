let currentProduct = null;
let currentQty = 1;
let wishlist = JSON.parse(localStorage.getItem("wishlist") || "[]");

document.addEventListener("DOMContentLoaded", () => {
    loadProduct();
});

// Load Product from URL Param
function loadProduct() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) { showError(); return; }

    fetch(`/api/products/${id}`, { credentials: "include" })
        .then(res => {
            if (!res.ok) throw new Error("Not found");
            return res.json();
        })
        .then(product => {
            currentProduct = product;
            renderProduct(product);
        })
        .catch(() => showError());
}

// Render Product
function renderProduct(p) {
    document.title = `PARTX — ${p.name}`;

    document.getElementById("pd-breadcrumb-name").textContent = p.name;
    document.getElementById("pd-image").src                   = p.imageUrl || "assets/placeholder.png";
    document.getElementById("pd-image").alt                   = p.name;
    document.getElementById("pd-category").textContent        = p.category || "General";
    document.getElementById("pd-name").textContent            = p.name;
    document.getElementById("pd-price").textContent           = `$${parseFloat(p.price).toFixed(2)}`;
    document.getElementById("pd-description").textContent     = p.description || "No description available.";

    // Stock badge
    const stockBadge = document.getElementById("pd-stock-badge");
    const cartBtn    = document.getElementById("pd-cart-btn");

    if (p.stock <= 0) {
        stockBadge.textContent = "Out of Stock";
        stockBadge.className   = "pd-stock-badge out-of-stock";
        cartBtn.disabled       = true;
        cartBtn.textContent    = "Out of Stock";
    } else if (p.stock <= 5) {
        stockBadge.textContent = `Only ${p.stock} left`;
        stockBadge.className   = "pd-stock-badge low-stock";
    } else {
        stockBadge.textContent = "In Stock";
        stockBadge.className   = "pd-stock-badge in-stock";
    }

    // Specs
    document.getElementById("spec-category").textContent = p.category || "—";
    document.getElementById("spec-stock").textContent    = p.stock;
    document.getElementById("spec-id").textContent       = `#${p.id}`;

    // Wishlist
    updateWishlistBtn(p.id);

    // Show content
    document.getElementById("pd-loading").style.display = "none";
    document.getElementById("pd-content").style.display = "flex";
}

// Quantity
function changeQty(delta) {
    if (!currentProduct) return;
    const max = currentProduct.stock > 0 ? currentProduct.stock : 1;
    currentQty = Math.min(max, Math.max(1, currentQty + delta));
    document.getElementById("pd-qty").textContent = currentQty;
}

// Add to Cart
function handleAddToCart() {
    const feedbackEl = document.getElementById("pd-feedback");
    feedbackEl.className  = "pd-feedback";
    feedbackEl.textContent = "";

    if (!currentProduct) return;

    const cart     = JSON.parse(localStorage.getItem("cart") || "[]");
    const existing = cart.find(item => item.id === currentProduct.id);

    if (existing) {
        existing.quantity = Math.min(currentProduct.stock, existing.quantity + currentQty);
    } else {
        cart.push({
            id:       currentProduct.id,
            name:     currentProduct.name,
            price:    currentProduct.price,
            imageUrl: currentProduct.imageUrl,
            quantity: currentQty
        });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    feedbackEl.textContent = `✓ Added ${currentQty} item${currentQty > 1 ? "s" : ""} to cart.`;
    setTimeout(() => { feedbackEl.textContent = ""; }, 3000);
}

// Wishlist
function toggleWishlist() {
    if (!currentProduct) return;
    const idx = wishlist.indexOf(currentProduct.id);
    if (idx === -1) {
        wishlist.push(currentProduct.id);
    } else {
        wishlist.splice(idx, 1);
    }
    localStorage.setItem("wishlist", JSON.stringify(wishlist));
    updateWishlistBtn(currentProduct.id);
}

function updateWishlistBtn(productId) {
    const btn = document.getElementById("pd-wishlist-btn");
    if (wishlist.includes(productId)) {
        btn.textContent = "♥";
        btn.classList.add("active");
    } else {
        btn.textContent = "♡";
        btn.classList.remove("active");
    }
}

// Error State
function showError() {
    document.getElementById("pd-loading").style.display = "none";
    document.getElementById("pd-error").style.display   = "flex";
}