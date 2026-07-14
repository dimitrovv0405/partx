let currentProduct = null;
let currentQty = 1;
let wishlist = JSON.parse(localStorage.getItem("wishlist") || "[]");

document.addEventListener("DOMContentLoaded", () => {
    // Read the product data directly out of the hidden DOM div populated by Thymeleaf
    const dataEl = document.getElementById("pd-data");
    if (dataEl) {
        currentProduct = {
            id: dataEl.getAttribute("th:data-id") || dataEl.dataset.id,
            name: dataEl.getAttribute("th:data-name") || dataEl.dataset.name,
            price: parseFloat(dataEl.getAttribute("th:data-price") || dataEl.dataset.price),
            stock: parseInt(dataEl.getAttribute("th:data-stock") || dataEl.dataset.stock),
            imageUrl: dataEl.getAttribute("th:data-image") || dataEl.dataset.image
        };

        // Initialize the wishlist button state
        updateWishlistBtn(currentProduct.id);
    }
});

// Quantity Control
function changeQty(delta) {
    if (!currentProduct) return;
    const max = currentProduct.stock > 0 ? currentProduct.stock : 1;
    currentQty = Math.min(max, Math.max(1, currentQty + delta));
    document.getElementById("pd-qty").textContent = currentQty;
}

// Add to Cart Logic (Local Storage)
function handleAddToCart() {
    const feedbackEl = document.getElementById("pd-feedback");
    if (!feedbackEl || !currentProduct) return;

    feedbackEl.className  = "pd-feedback";
    feedbackEl.textContent = "";

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

// Wishlist Control
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
    if (!btn) return;

    if (wishlist.includes(productId)) {
        btn.textContent = "♥";
        btn.classList.add("active");
    } else {
        btn.textContent = "♡";
        btn.classList.remove("active");
    }
}
