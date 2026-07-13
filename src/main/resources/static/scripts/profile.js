function switchTab(buttonElement) {
    // Remove active class from all buttons and tabs
    document.querySelectorAll('.profile-nav-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.profile-tab').forEach(tab => tab.classList.remove('active'));

    // Add active class to the clicked button
    buttonElement.classList.add('active');

    // Show the corresponding tab
    const tabId = buttonElement.getAttribute('data-tab');
    document.getElementById('tab-' + tabId).classList.add('active');
}

function showMoneyInput() {
    const addMoneyForm = document.getElementById('add-money-form');
    const initiateBtn = document.getElementById('initiate-add-btn');

    if (addMoneyForm && initiateBtn) {
        addMoneyForm.style.display = 'flex';
        initiateBtn.style.display = 'none';
    }
}

function submitAvatarForm() {
    const fileInput = document.getElementById('avatar-upload');
    const form = document.getElementById('avatar-form');

    if (fileInput && fileInput.files && fileInput.files.length > 0) {
        form.submit();
    }
}