// --- Tab Switching ---
function switchTab(element) {
    document.querySelectorAll(".freelance-nav-btn").forEach(btn => btn.classList.remove("active"));
    document.querySelectorAll(".freelance-tab").forEach(tab => tab.classList.remove("active"));

    element.classList.add("active");
    const targetTab = document.getElementById(`tab-${element.dataset.tab}`);
    if (targetTab) {
        targetTab.classList.add("active");
    }
}

// =========================================================
// PRODUCTS (Add / Edit / Delete)
// =========================================================
function openProductModal() {
    document.getElementById('product-id').value = '';
    document.getElementById('product-name').value = '';
    document.getElementById('product-description').value = '';
    document.getElementById('product-price').value = '';
    document.getElementById('product-stock').value = '';
    document.getElementById('product-category').value = '';
    document.getElementById('product-image').value = '';

    document.getElementById('product-modal-title').innerText = 'Add Product';
    document.getElementById('product-submit-btn').innerText = 'Add Product';
    document.getElementById('product-modal-overlay').classList.add('active');
}

function editProduct(button) {
    const id = button.getAttribute('data-id');
    const name = button.getAttribute('data-name');
    const description = button.getAttribute('data-description');
    const price = button.getAttribute('data-price');
    const stock = button.getAttribute('data-stock');
    const categoryId = button.getAttribute('data-category-id');
    const imageUrl = button.getAttribute('data-image-url');

    document.getElementById('product-id').value = id || '';
    document.getElementById('product-name').value = name || '';
    document.getElementById('product-description').value = description || '';
    document.getElementById('product-price').value = price || '';
    document.getElementById('product-stock').value = stock || '';
    document.getElementById('product-category').value = categoryId || '';
    document.getElementById('product-image').value = imageUrl || '';

    document.getElementById('product-modal-title').innerText = 'Edit Product';
    document.getElementById('product-submit-btn').innerText = 'Save Changes';
    document.getElementById('product-modal-overlay').classList.add('active');
}

function closeProductModal() {
    const overlay = document.getElementById("product-modal-overlay");
    overlay.classList.remove("active");
    overlay.style.display = "";
}

function confirmDeleteProduct(id) {
    document.getElementById('delete-product-id').value = id;
    document.getElementById('confirm-modal-overlay').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirm-modal-overlay').classList.remove('active');
}

// Client-side search filtering for Products table
function renderProducts() {
    const query = document.getElementById("product-search").value.toLowerCase();
    const rows = document.querySelectorAll("#products-tbody tr[data-name]");
    let visibleCount = 0;

    rows.forEach(row => {
        const name = row.getAttribute("data-name") || "";
        if (name.includes(query)) {
            row.style.display = "";
            visibleCount++;
        } else {
            row.style.display = "none";
        }
    });

    const emptyMsg = document.getElementById("products-empty");
    if (emptyMsg) {
        emptyMsg.style.display = visibleCount === 0 ? "block" : "none";
    }
}

// =========================================================
// ORDERS (Search & Filter)
// =========================================================
function renderAdminOrders() {
    const query = document.getElementById("order-search").value.toLowerCase();
    const statusFilter = document.getElementById("order-status-filter").value;
    const rows = document.querySelectorAll("#orders-tbody tr[data-search]");
    let visibleCount = 0;

    rows.forEach(row => {
        const searchData = row.getAttribute("data-search") || "";
        const statusData = row.getAttribute("data-status") || "";

        const matchesSearch = searchData.includes(query);
        const matchesStatus = statusFilter === "all" || statusData === statusFilter;

        if (matchesSearch && matchesStatus) {
            row.style.display = "";
            visibleCount++;
        } else {
            row.style.display = "none";
        }
    });

    const emptyMsg = document.getElementById("orders-empty");
    if (emptyMsg) {
        emptyMsg.style.display = visibleCount === 0 ? "block" : "none";
    }
}

function openOrderModal(orderId, currentStatus) {
    document.getElementById('order-modal-id').value = orderId;
    document.getElementById('order-status-select').value = currentStatus;
    document.getElementById('order-modal-overlay').classList.add('active');
}

function closeOrderModal() {
    document.getElementById('order-modal-overlay').classList.remove('active');
}