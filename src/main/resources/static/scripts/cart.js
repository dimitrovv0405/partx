const SHIPPING_RATE = 5.99;

document.addEventListener("DOMContentLoaded", () => {
    renderCart();
});

function getCart() {
    return JSON.parse(localStorage.getItem("cart")) || [];
}

function saveCart(cart) {
    localStorage.setItem("cart", JSON.stringify(cart));
}

function renderCart() {
    const cart        = getCart();
    const list        = document.getElementById("cart-items-list");
    const emptyMsg    = document.getElementById("empty-cart-msg");
    const checkoutBtn = document.getElementById("checkout-btn");

    list.innerHTML = "";

    if (cart.length === 0) {
        emptyMsg.style.display = "block";
        checkoutBtn.disabled = true;
        updateSummary(0);
        return;
    }

    emptyMsg.style.display = "none";
    checkoutBtn.disabled = false;

    cart.forEach((item, index) => {
        const itemSubtotal = (parseFloat(item.price) * item.quantity).toFixed(2);
        const div = document.createElement("div");
        div.className = "cart-item";
        div.innerHTML = `
            <div class="cart-item-info">
                <p class="cart-item-name">${item.name}</p>
                <p class="cart-item-price">$${parseFloat(item.price).toFixed(2)} each</p>
            </div>
            <div class="quantity-controls">
                <button class="qty-btn" onclick="changeQuantity(${index}, -1)">−</button>
                <span class="qty-value">${item.quantity}</span>
                <button class="qty-btn" onclick="changeQuantity(${index}, 1)">+</button>
            </div>
            <p class="cart-item-subtotal">$${itemSubtotal}</p>
            <button class="remove-item-btn" onclick="removeItem(${index})" title="Remove">✕</button>
        `;
        list.appendChild(div);
    });

    const subtotal = cart.reduce((sum, item) =>
        sum + parseFloat(item.price) * item.quantity, 0);
    updateSummary(subtotal);
}

function updateSummary(subtotal) {
    const shipping = subtotal > 0 ? SHIPPING_RATE : 0;
    const total    = subtotal + shipping;

    document.getElementById("subtotal").textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById("shipping").textContent = `$${shipping.toFixed(2)}`;
    document.getElementById("total").textContent    = `$${total.toFixed(2)}`;
}

function changeQuantity(index, delta) {
    const cart = getCart();
    cart[index].quantity += delta;

    if (cart[index].quantity <= 0) {
        cart.splice(index, 1);
    }

    saveCart(cart);
    renderCart();
}

function removeItem(index) {
    const cart = getCart();
    cart.splice(index, 1);
    saveCart(cart);
    renderCart();
}

function clearCart() {
    localStorage.removeItem("cart");
    renderCart();
}

async function handleCheckout() {
    const cart = getCart();
    if (cart.length === 0) return;

    const errorMsg    = document.getElementById("checkout-error");
    const successMsg  = document.getElementById("checkout-success");
    const checkoutBtn = document.getElementById("checkout-btn");

    // Reset messages
    errorMsg.textContent   = "";
    errorMsg.style.display = "none";
    successMsg.textContent   = "";
    successMsg.style.display = "none";

    checkoutBtn.disabled    = true;
    checkoutBtn.textContent = "Placing Order...";

    const orderPayload = {
        items: cart.map(item => ({
            productId: item.id,
            quantity:  item.quantity
        }))
    };

    try {
        const response = await fetch("/api/orders", {
            method:  "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(orderPayload)
        });

        if (response.ok) {
            successMsg.textContent   = "Order placed! Redirecting to orders...";
            successMsg.style.display = "block";
            localStorage.removeItem("cart");
            setTimeout(() => {
                window.location.href = "/orders";
            }, 1500);
        } else {
            const data = await response.json().catch(() => null);
            errorMsg.textContent   = data?.message || "Failed to place order. Please try again.";
            errorMsg.style.display = "block";
        }
    } catch {
        errorMsg.textContent   = "Something went wrong. Please try again.";
        errorMsg.style.display = "block";
    } finally {
        checkoutBtn.disabled    = false;
        checkoutBtn.textContent = "Place Order";
    }
}