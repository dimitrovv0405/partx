let selectedCategoryId = "all";
let allProductCards = [];

document.addEventListener("DOMContentLoaded", () => {
    allProductCards = Array.from(document.querySelectorAll(".product-card"));

    const activeItem = document.querySelector(".filter-item.active");
    if (activeItem) {
        selectedCategoryId = activeItem.getAttribute("data-id");
    }
});

function selectCategory(el) {
    document.querySelectorAll(".filter-item").forEach(i => i.classList.remove("active"));
    el.classList.add("active");
    selectedCategoryId = el.getAttribute("data-id");
    applyFilters();
}

function applyFilters() {
    const search = document.getElementById("search-input").value.toLowerCase();
    const minPrice = parseFloat(document.getElementById("min-price").value) || 0;
    const maxPrice = parseFloat(document.getElementById("max-price").value) || Infinity;
    const inStockOnly = document.getElementById("in-stock-only").checked;
    const sortValue = document.getElementById("sort-select").value;

    // 💡 CHANGE HERE: Filter against the saved MASTER list, not the modified DOM grid
    let filtered = allProductCards.filter(card => {
        const name = card.getAttribute("data-name").toLowerCase();
        const price = parseFloat(card.getAttribute("data-price"));
        const categoryId = card.getAttribute("data-category");
        const stock = parseInt(card.getAttribute("data-stock"));

        const matchSearch = name.includes(search);
        const matchPrice = price >= minPrice && price <= maxPrice;
        const matchCategory = selectedCategoryId === "all" || categoryId === selectedCategoryId;
        const matchStock = !inStockOnly || stock > 0;

        return matchSearch && matchPrice && matchCategory && matchStock;
    });

    // Sorting
    filtered.sort((a, b) => {
        const nameA = a.getAttribute("data-name").toLowerCase();
        const nameB = b.getAttribute("data-name").toLowerCase();
        const priceA = parseFloat(a.getAttribute("data-price"));
        const priceB = parseFloat(b.getAttribute("data-price"));

        if (sortValue === "price-asc") return priceA - priceB;
        if (sortValue === "price-desc") return priceB - priceA;
        if (sortValue === "name-asc") return nameA.localeCompare(nameB);
        if (sortValue === "name-desc") return nameB.localeCompare(nameA);
        return 0;
    });

    const grid = document.getElementById("products-grid");
    grid.innerHTML = "";
    filtered.forEach(card => grid.appendChild(card));

    document.getElementById("results-count").textContent = filtered.length + " Results";
}

function clearFilters() {
    document.getElementById("search-input").value = "";
    document.getElementById("min-price").value = "";
    document.getElementById("max-price").value = "";
    document.getElementById("in-stock-only").checked = false;
    document.getElementById("sort-select").value = "default";
    selectedCategoryId = "all";
    document.querySelectorAll(".filter-item").forEach(i => i.classList.remove("active"));
    document.querySelector(".filter-item[data-id='all']").classList.add("active");
    applyFilters();
}

function addToCart(btn) {
    const id = btn.getAttribute("data-id");
    const name = btn.getAttribute("data-name");
    const price = parseFloat(btn.getAttribute("data-price"));

    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    const existing = cart.find(item => item.id === id);

    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({ id, name, price, quantity: 1 });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    alert(`${name} added to cart!`);
}