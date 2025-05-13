function addToCart(button) {

    let productId = button.getAttribute("data-id");
    let productName = button.getAttribute("data-name");
    let price = button.getAttribute("data-price");

    let cart = JSON.parse(localStorage.getItem('cart')) || {
        items: []
    };

    const existingItem = cart.items.find(item => item.productId === productId);

    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.items.push({
            productId,
            productName,
            price,
            quantity: 1
        });
    }

    cart.total = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCounter();

    let toastEl = document.getElementById("cart-toast");
    let toast = new bootstrap.Toast(toastEl);
    toast.show();
}

// Вспомогательные функции
function updateCartCounter() {
    const cart = JSON.parse(localStorage.getItem('cart')) || { items: [] };
    document.querySelectorAll('.cart-counter').forEach(el => {
        el.textContent = cart.items.reduce((sum, item) => sum + item.quantity, 0);
    });
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', updateCartCounter);

document.addEventListener("DOMContentLoaded", function () {

    let success_time_stamp = localStorage.getItem("success-time-stamp");

    if ((Date.now()-success_time_stamp) < 3000 ) {
        let successModal = new bootstrap.Modal(document.getElementById('successModal'));
        successModal.show();
        localStorage.removeItem("success-time-stamp")

        // Очищаем URL, чтобы pop-up не показывался при перезагрузке
        window.history.replaceState(null, null, window.location.pathname);
    }
});







// <!--       <div class="col-md-12">-->
// <!--            <nav aria-label="Page navigation">-->
// <!--                <ul class="pagination justify-content-center">-->
// <!--                    <li class="page-item" th:classappend="${page.first} ? 'disabled' : ''">-->
// <!--                        <a class="page-link"-->
// <!--                           th:href="@{/appliances(category=${param.category}, sort=${param.sort}, discounted=${param.discounted}, page=${page.number - 1})}"-->
// <!--                            th:text="#{general.btn.back}">-->
// <!--                        </a>-->
// <!--                    </li>-->
//
// <!--                    <li th:each="i : ${#numbers.sequence(1, page.totalPages)}"-->
// <!--                        class="page-item"-->
// <!--                        th:classappend="${page.number + 1 == i} ? 'active' : ''">-->
// <!--                        <a class="page-link"-->
// <!--                           th:href="@{/appliances(category=${param.category}, sort=${param.sort}, discounted=${param.discounted}, page=${i - 1})}"-->
// <!--                           th:text="${i}"></a>-->
// <!--                    </li>-->
//
// <!--                    <li class="page-item" th:classappend="${page.last} ? 'disabled' : ''">-->
// <!--                        <a class="page-link"-->
// <!--                           th:href="@{/appliances(category=${param.category}, sort=${param.sort}, discounted=${param.discounted}, page=${page.number + 1})}"-->
// <!--                            th:text="#{general.btn.next}">-->
// <!--                        </a>-->
// <!--                    </li>-->
// <!--                </ul>-->
// <!--            </nav>-->
// <!--        </div>-->
// <!--    </div>-->
// <!--</div>-->


