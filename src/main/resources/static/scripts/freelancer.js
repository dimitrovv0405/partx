let allProducts = [];
let allAdminOrders = [];
let allUsers = [];

document.addEventListener("DOMContentLoaded", () => {
    loadProducts();
    loadAdminOrders();
    loadUsers();
});

// --- Tab Switch ---
// --- Tab Switch ---
function switchTab(element) {
    document.querySelectorAll(".freelance-nav-btn").forEach(btn => btn.classList.remove("active"));
    document.querySelectorAll(".freelance-tab").forEach(tab => tab.classList.remove("active"));

    element.classList.add("active");
    document.getElementById(`tab-${element.dataset.tab}`).classList.add("active");
}

// =========================================================
// PRODUCTS
// =========================================================
function loadProducts() {
    fetch("/api/products", { credentials: "include" })
        .then(res => res.json())
        .then(data => { allProducts = data; renderProducts(); })
        .catch(() => console.error("Failed to load products."));
}

function renderProducts() {
    const query = document.getElementById("product-search").value.toLowerCase();
    const tbody = document.getElementById("products-tbody");
    const empty = document.getElementById("products-empty");

    let filtered = allProducts.filter(p =>
        p.name.toLowerCase().includes(query) ||
        (p.category?.name || "").toLowerCase().includes(query)
    );

    tbody.innerHTML = "";

    if (filtered.length === 0) {
        empty.style.display = "block";
        return;
    }
    empty.style.display = "none";

    filtered.forEach(p => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><span style="font-family:'Space Mono',monospace;font-size:12px;color:#6a6a6a;">#${p.id}</span></td>
            <td>${p.name}</td>
            <td>${p.category?.name || "—"}</td>
            <td style="font-family:'Space Mono',monospace;color:#c8dc00;">$${parseFloat(p.price).toFixed(2)}</td>
            <td>${p.stockAmount}</td>
            <td>
                <button class="tbl-btn tbl-btn-edit" onclick="openProductModal(${JSON.stringify(p).replace(/"/g, '&quot;')})">Edit</button>
                <button class="tbl-btn tbl-btn-delete" onclick="openConfirmModal('product', '${p.id}', '${p.name}')">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openProductModal(product = null) {
    document.getElementById("product-form-error").textContent = "";
    document.getElementById("product-form").reset();

    if (product) {
        document.getElementById("product-modal-title").textContent = "Edit Product";
        document.getElementById("product-submit-btn").textContent  = "Save Changes";
        document.getElementById("product-id").value          = product.id;
        document.getElementById("product-name").value        = product.name;
        document.getElementById("product-description").value = product.description || "";
        document.getElementById("product-price").value       = product.price;
        document.getElementById("product-stock").value       = product.stockAmount; // CHANGED
        document.getElementById("product-category").value    = product.category?.id || "";
        document.getElementById("product-image").value       = product.imageUrl || "";
    } else {
        document.getElementById("product-modal-title").textContent = "Add Product";
        document.getElementById("product-submit-btn").textContent  = "Add Product";
        document.getElementById("product-id").value = "";
    }

    document.getElementById("product-modal-overlay").style.display = "flex";
}

function closeProductModal() {
    document.getElementById("product-modal-overlay").style.display = "none";
}

// Commented out since not needed
//async function handleProductSubmit(event) {
//    event.preventDefault();
//    const errorEl = document.getElementById("product-form-error");
//    errorEl.textContent = "";
//
//    const id = document.getElementById("product-id").value;
//    const payload = {
//        name:        document.getElementById("product-name").value.trim(),
//        description: document.getElementById("product-description").value.trim(),
//        price:       parseFloat(document.getElementById("product-price").value),
//        stock:       parseInt(document.getElementById("product-stock").value),
//        categoryId:  document.getElementById("product-category").value || null,
//        imageUrl:    document.getElementById("product-image").value.trim()
//    };
//
//    const url    = id ? `/api/products/${id}` : "/api/products";
//    const method = id ? "PUT" : "POST";
//
//    try {
//        const res = await fetch(url, {
//            method,
//            headers: { "Content-Type": "application/json" },
//            credentials: "include",
//            body: JSON.stringify(payload)
//        });
//
//        if (res.ok) {
//            closeProductModal();
//            loadProducts();
//        } else {
//            const msg = await res.text();
//            errorEl.textContent = msg || "Failed to save product.";
//        }
//    } catch {
//        errorEl.textContent = "Network error. Please try again.";
//    }
//}

// =========================================================
// ORDERS
// =========================================================
function loadAdminOrders() {
    fetch("/api/orders/admin/all", { credentials: "include" }) // CHANGED
        .then(res => res.json())
        .then(data => { allAdminOrders = data; renderAdminOrders(); })
        .catch(() => console.error("Failed to load orders."));
}

function renderAdminOrders() {
    const query        = document.getElementById("order-search").value.toLowerCase();
    const statusFilter = document.getElementById("order-status-filter").value;
    const tbody        = document.getElementById("orders-tbody");
    const empty        = document.getElementById("orders-empty");

    let filtered = allAdminOrders.filter(o => {
        const matchSearch = String(o.id).includes(query) ||
            (o.user?.username || "").toLowerCase().includes(query);
        const matchStatus = statusFilter === "all" || o.status === statusFilter;
        return matchSearch && matchStatus;
    });

    tbody.innerHTML = "";

    if (filtered.length === 0) {
        empty.style.display = "block";
        return;
    }
    empty.style.display = "none";

    filtered.forEach(o => {
        const date = new Date(o.createdAt).toLocaleDateString("en-US", {
            year: "numeric", month: "short", day: "numeric"
        });
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><span style="font-family:'Space Mono',monospace;font-size:12px;color:#6a6a6a;">#${o.id}</span></td>
            <td>${o.user?.username || "—"}</td>
            <td>${date}</td>
            <td style="font-family:'Space Mono',monospace;color:#c8dc00;">$${parseFloat(o.totalPrice).toFixed(2)}</td>
            <td><span class="order-status-badge status-${o.status.toLowerCase()}">${o.status}</span></td>
            <td>
                <button class="tbl-btn tbl-btn-edit" onclick="openOrderModal('${o.id}', '${o.status}')">Update</button>
                <button class="tbl-btn tbl-btn-delete" onclick="openConfirmModal('order', '${o.id}', 'Order #${o.id}')">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openOrderModal(orderId, currentStatus) {
    document.getElementById("order-form-error").textContent = "";
    document.getElementById("order-modal-id").value         = orderId;
    document.getElementById("order-status-select").value    = currentStatus;
    document.getElementById("order-modal-overlay").style.display = "flex";
}

function closeOrderModal() {
    document.getElementById("order-modal-overlay").style.display = "none";
}

async function handleOrderStatusUpdate() {
    const errorEl   = document.getElementById("order-form-error");
    const orderId   = document.getElementById("order-modal-id").value;
    const newStatus = document.getElementById("order-status-select").value;

    errorEl.textContent = "";

    try {
        const res = await fetch(`/api/orders/${orderId}/status`, {
            method: "PATCH", // CHANGED from PUT to PATCH
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ status: newStatus })
        });

        if (res.ok) {
            closeOrderModal();
            loadAdminOrders();
        } else {
            const msg = await res.text();
            errorEl.textContent = msg || "Failed to update status.";
        }
    } catch {
        errorEl.textContent = "Network error. Please try again.";
    }
}

// =========================================================
// USERS
// =========================================================
function loadUsers() {
    fetch("/api/users", { credentials: "include" })
        .then(res => res.json())
        .then(data => { allUsers = data; renderUsers(); })
        .catch(() => console.error("Failed to load users."));
}

function renderUsers() {
    const query = document.getElementById("user-search").value.toLowerCase();
    const tbody = document.getElementById("users-tbody");
    const empty = document.getElementById("users-empty");

    let filtered = allUsers.filter(u =>
        u.username.toLowerCase().includes(query) ||
        u.email.toLowerCase().includes(query)
    );

    tbody.innerHTML = "";

    if (filtered.length === 0) {
        empty.style.display = "block";
        return;
    }
    empty.style.display = "none";

    filtered.forEach(u => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><span style="font-family:'Space Mono',monospace;font-size:12px;color:#6a6a6a;">#${u.id}</span></td>
            <td>${u.username}</td>
            <td>${u.email}</td>
            <td><span class="role-badge role-${u.role.toLowerCase()}">${u.role}</span></td>
            <td>
                <button class="tbl-btn tbl-btn-delete" onclick="openConfirmModal('user', '${u.id}', '${u.username}')">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// =========================================================
// CONFIRM / DELETE MODAL
// =========================================================
let pendingDelete = { type: null, id: null };

function openConfirmModal(type, id, label) {
    pendingDelete = { type, id };
    document.getElementById("confirm-msg").textContent =
        `Are you sure you want to delete ${type} "${label}"? This action cannot be undone.`;
    document.getElementById("confirm-modal-overlay").style.display = "flex";
}

function closeConfirmModal() {
    pendingDelete = { type: null, id: null };
    document.getElementById("confirm-modal-overlay").style.display = "none";
}

document.getElementById("confirm-delete-btn").addEventListener("click", async () => {
    const { type, id } = pendingDelete;
    if (!type || !id) return;

    const endpoints = {
        product: `/api/products/${id}`,
        order:   `/api/orders/${id}/admin`, // CHANGED
        user:    `/api/users/${id}`
    };

    try {
        const res = await fetch(endpoints[type], {
            method: "DELETE",
            credentials: "include"
        });

        closeConfirmModal();

        if (res.ok) {
            if (type === "product") loadProducts();
            if (type === "order")   loadAdminOrders();
            if (type === "user")    loadUsers();
        } else {
            alert("Delete failed. You may not have permission.");
        }
    } catch {
        alert("Network error. Please try again.");
    }
});