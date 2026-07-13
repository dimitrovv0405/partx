 let selectedStatus = "all";

 document.addEventListener("DOMContentLoaded", () => {
     renderOrders();
 });

 function selectStatus(btn) {
     document.querySelectorAll(".status-filter-btn").forEach(b => b.classList.remove("active"));
     btn.classList.add("active");
     selectedStatus = btn.getAttribute("data-status");
     renderOrders();
 }

 function renderOrders() {
     const sortValue = document.getElementById("sort-select").value;
     const allCards = Array.from(document.querySelectorAll(".order-card"));

     let filtered = allCards.filter(card => {
         const status = card.getAttribute("data-status");
         return selectedStatus === "all" || status === selectedStatus;
     });

     filtered.sort((a, b) => {
         const dateA = new Date(a.getAttribute("data-date"));
         const dateB = new Date(b.getAttribute("data-date"));
         const totalA = parseFloat(a.getAttribute("data-total"));
         const totalB = parseFloat(b.getAttribute("data-total"));

         if (sortValue === "newest") return dateB - dateA;
         if (sortValue === "oldest") return dateA - dateB;
         if (sortValue === "total-desc") return totalB - totalA;
         if (sortValue === "total-asc") return totalA - totalB;
         return 0;
     });

     const list = document.getElementById("orders-list");
     list.innerHTML = "";
     filtered.forEach(card => list.appendChild(card));

     const emptyMsg = document.getElementById("empty-orders-msg");
     if (emptyMsg) {
         emptyMsg.style.display = filtered.length === 0 ? "block" : "none";
     }
 }

 async function openModal(btn) {
     const orderId = btn.getAttribute("data-id");
     const modalOverlay = document.getElementById("modal-overlay");
     const modalContent = document.getElementById("modal-content");

     modalContent.innerHTML = "<p>Loading...</p>";
     modalOverlay.style.display = "flex";

     try {
         const response = await fetch(`/api/orders/${orderId}`, {
             credentials: "include"
         });

         if (!response.ok) {
             modalContent.innerHTML = "<p>Failed to load order details.</p>";
             return;
         }

         const order = await response.json();

         const itemsHTML = order.items.map(item => `
             <div class="modal-item">
                 <span class="modal-item-name">${item.productName}</span>
                 <span class="modal-item-qty">x${item.quantity}</span>
                 <span class="modal-item-price">$${parseFloat(item.price).toFixed(2)}</span>
             </div>
         `).join("");

         modalContent.innerHTML = `
             <p><strong>Order #${order.id}</strong></p>
             <p>Status: <span class="order-status-badge status-${order.status.toLowerCase()}">${order.status}</span></p>
             <p>Date: ${new Date(order.createdAt).toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" })}</p>
             <div class="modal-items-list">${itemsHTML}</div>
             <p class="modal-total"><strong>Total: $${parseFloat(order.totalPrice).toFixed(2)}</strong></p>
         `;
     } catch {
         modalContent.innerHTML = "<p>Something went wrong. Please try again.</p>";
     }
 }

 function closeModal() {
     document.getElementById("modal-overlay").style.display = "none";
     document.getElementById("modal-content").innerHTML = "";
 }