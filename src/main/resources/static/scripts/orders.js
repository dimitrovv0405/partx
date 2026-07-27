let selectedStatus = "all";
let allCards = [];

document.addEventListener("DOMContentLoaded", () => {
    // Cache original cards from DOM on initial page load
    const container = document.getElementById("orders-list");
    if (container) {
        allCards = Array.from(container.querySelectorAll(".order-card"));
    }
    renderOrders();
});

function selectStatus(btn) {
    document.querySelectorAll(".status-filter-btn").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");
    selectedStatus = btn.getAttribute("data-status");
    renderOrders();
}

function renderOrders() {
    const list = document.getElementById("orders-list");
    if (!list) return;

    const sortSelect = document.getElementById("sort-select");
    const sortValue = sortSelect ? sortSelect.value : "newest";

    // 1. Filter by status
    let filtered = allCards.filter(card => {
        const cardStatus = card.getAttribute("data-status");
        if (!selectedStatus || selectedStatus.toLowerCase() === "all") return true;
        return cardStatus && cardStatus.toUpperCase() === selectedStatus.toUpperCase();
    });

    // 2. Sort by selected dropdown value
    filtered.sort((a, b) => {
        const totalA = parseFloat(a.getAttribute("data-price") || a.getAttribute("data-total")) || 0;
        const totalB = parseFloat(b.getAttribute("data-price") || b.getAttribute("data-total")) || 0;

        // Helper function to safely extract a numeric timestamp
        const parseDate = (card) => {
            const rawDate = card.getAttribute("data-date");
            if (rawDate && !isNaN(Date.parse(rawDate))) {
                return new Date(rawDate).getTime();
            }
            // Fallback to numeric order ID if available, otherwise 0
            const rawId = card.getAttribute("data-id") || "0";
            const numericId = parseInt(rawId.replace(/\D/g, ""), 10);
            return isNaN(numericId) ? 0 : numericId;
        };

        const dateA = parseDate(a);
        const dateB = parseDate(b);

        switch (sortValue) {
            case "newest":
                return dateB - dateA;
            case "oldest":
                return dateA - dateB;
            case "total-desc":
                return totalB - totalA;
            case "total-asc":
                return totalA - totalB;
            default:
                return 0;
        }
    });

    // 3. Render DOM nodes cleanly
    list.innerHTML = "";
    filtered.forEach(card => {
        card.style.display = "flex";
        list.appendChild(card);
    });

    // 4. Manage empty state visibility
    const emptyMsg = document.getElementById("empty-orders-msg");
    if (emptyMsg) {
        emptyMsg.style.display = filtered.length === 0 ? "block" : "none";
    }
}

async function openModal(btn) {
const orderId = btn.getAttribute("data-id");
    const modalOverlay = document.getElementById("modal-overlay");
    const modalContent = document.getElementById("modal-content");

    if (!modalOverlay || !modalContent) return;

    modalContent.innerHTML = `<p style="color: #888; text-align: center; font-family: monospace;">Loading order details...</p>`;
    modalOverlay.style.display = "flex";

    try {
        // Calls the ModelAndView HTML fragment controller endpoint
        const response = await fetch(`/orders/modal/${orderId}`);

        if (!response.ok) {
            modalContent.innerHTML = `<p style="color: #ff3e3e; text-align: center;">Failed to load order details.</p>`;
            return;
        }

        const html = await response.text();
        modalContent.innerHTML = html;

    } catch (err) {
        console.error("Fetch error:", err);
        modalContent.innerHTML = `<p style="color: #ff3e3e; text-align: center;">Something went wrong. Please try again.</p>`;
    }
}

function closeModal() {
    const modalOverlay = document.getElementById("modal-overlay");
    const modalContent = document.getElementById("modal-content");

    if (modalOverlay) modalOverlay.style.display = "none";
    if (modalContent) modalContent.innerHTML = "";
}