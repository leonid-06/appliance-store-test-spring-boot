let cart = JSON.parse(localStorage.getItem('cart')) || {items: []};

// message.properties
let currency = "UAN"
let error_title = "Something went wrong. Rebuild your cart again"

function renderCart() {
    const cartItemsContainer = document.getElementById('cart-items');
    const cartEmpty = document.getElementById('cart-empty');
    const cartContent = document.getElementById('cart-content');
    const cartTotal = document.getElementById('cart-total');
    const placeForButton = document.getElementById('confirm-order');

    if (cart.items.length === 0) {
        cartEmpty.style.display = 'block';
        cartContent.style.display = 'none';
        placeForButton.disable()
        return;
    }

    cartEmpty.style.display = 'none';
    cartContent.style.display = 'block';
    cartItemsContainer.innerHTML = '';

    let total = 0;

    cart.items.forEach((item, index) => {
        const sum = (item.price * item.quantity);
        total += sum;
        const row = document.createElement('tr');
        row.innerHTML = `
                    <td>${item.productName}</td>
                    <td>${item.price} ${currency}}.</td>
                    <td>
                        <div class="input-group input-group-sm" style="width: 120px;">
                            <button class="btn btn-outline-secondary minus-btn" data-index="${index}">
                                <i class="bi bi-dash"></i>
                            </button>
                            <input type="number" class="form-control quantity-input"
                                   value="${item.quantity}" min="1" data-index="${index}">
                            <button class="btn btn-outline-secondary plus-btn" data-index="${index}">
                                <i class="bi bi-plus"></i>
                            </button>
                        </div>
                    </td>
                    <td>${sum.toFixed(2)} ${currency}.</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger remove-btn" data-index="${index}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                `;
        cartItemsContainer.appendChild(row);
    });

    cartTotal.textContent = `${total.toFixed(2)} ${currency}.`;
}

document.addEventListener('click', (e) => {
    if (e.target.closest('.minus-btn')) {
        const index = e.target.closest('.minus-btn').dataset.index;
        if (cart.items[index].quantity > 1) {
            cart.items[index].quantity--;
            localStorage.setItem('cart', JSON.stringify(cart));
            renderCart();
        }
    }

    if (e.target.closest('.plus-btn')) {
        const index = e.target.closest('.plus-btn').dataset.index;
        cart.items[index].quantity++;
        localStorage.setItem('cart', JSON.stringify(cart));
        renderCart();
    }

    if (e.target.closest('.remove-btn')) {
        const index = e.target.closest('.remove-btn').dataset.index;
        cart.items.splice(index, 1);
        localStorage.setItem('cart', JSON.stringify(cart));
        renderCart();
    }
});

document.addEventListener('change', (e) => {
    if (e.target.classList.contains('quantity-input')) {
        const index = e.target.dataset.index;
        const newQuantity = parseInt(e.target.value);

        if (newQuantity >= 1) {
            cart.items[index].quantity = newQuantity;
            localStorage.setItem('cart', JSON.stringify(cart));
            renderCart();
        } else {
            e.target.value = cart.items[index].quantity;
        }
    }
});

document.getElementById('confirm-order').addEventListener('click', async () => {
    if (cart.items.length === 0) {
        alert('Корзина пуста!');
        return;
    }

    const calculatedOnFrontTotal = parseFloat(
        cart.items.reduce((sum, item) => {
            return sum + item.quantity * item.price;
        }, 0).toFixed(2)
    );

    const orderData = {
        orderRowSet: cart.items.map(item => ({
            applianceId: item.productId,
            number: item.quantity
        })),
        calculatedOnFrontTotal
    };

    try {
        const response = await fetch('/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });

        if (response.ok) {
            localStorage.setItem("success-time-stamp", Date.now());
            localStorage.removeItem('cart');
            window.location.href = '/cabinet?isCreated=true';
        } else {
            alert(`${error_title}`);
        }
    } catch (error) {
        alert(`${error_title}`);
    }
});

renderCart();













// let cart = JSON.parse(localStorage.getItem('cart')) || {items: []};
//
// // message.properties
// let currency = [[#{currency}]];
// let error_title = [[#{cart.order.error}]];
//
// function renderCart() {
//     const cartItemsContainer = document.getElementById('cart-items');
//     const cartEmpty = document.getElementById('cart-empty');
//     const cartContent = document.getElementById('cart-content');
//     const cartTotal = document.getElementById('cart-total');
//     const placeForButton = document.getElementById('confirm-order');
//
//     if (cart.items.length === 0) {
//         cartEmpty.style.display = 'block';
//         cartContent.style.display = 'none';
//         placeForButton.disable()
//         return;
//     }
//
//     cartEmpty.style.display = 'none';
//     cartContent.style.display = 'block';
//     cartItemsContainer.innerHTML = '';
//
//     let total = 0;
//
//     cart.items.forEach((item, index) => {
//         const sum = (item.price * item.quantity);
//         total += sum;
//         const row = document.createElement('tr');
//         row.innerHTML = `
//                     <td>${item.productName}</td>
//                     <td>${item.price} ${currency}}.</td>
//                     <td>
//                         <div class="input-group input-group-sm" style="width: 120px;">
//                             <button class="btn btn-outline-secondary minus-btn" data-index="${index}">
//                                 <i class="bi bi-dash"></i>
//                             </button>
//                             <input type="number" class="form-control quantity-input"
//                                    value="${item.quantity}" min="1" data-index="${index}">
//                             <button class="btn btn-outline-secondary plus-btn" data-index="${index}">
//                                 <i class="bi bi-plus"></i>
//                             </button>
//                         </div>
//                     </td>
//                     <td>${sum.toFixed(2)} ${currency}.</td>
//                     <td>
//                         <button class="btn btn-sm btn-outline-danger remove-btn" data-index="${index}">
//                             <i class="bi bi-trash"></i>
//                         </button>
//                     </td>
//                 `;
//         cartItemsContainer.appendChild(row);
//     });
//
//     cartTotal.textContent = `${total.toFixed(2)} ${currency}.`;
// }
//
// document.addEventListener('click', (e) => {
//     if (e.target.closest('.minus-btn')) {
//         const index = e.target.closest('.minus-btn').dataset.index;
//         if (cart.items[index].quantity > 1) {
//             cart.items[index].quantity--;
//             localStorage.setItem('cart', JSON.stringify(cart));
//             renderCart();
//         }
//     }
//
//     if (e.target.closest('.plus-btn')) {
//         const index = e.target.closest('.plus-btn').dataset.index;
//         cart.items[index].quantity++;
//         localStorage.setItem('cart', JSON.stringify(cart));
//         renderCart();
//     }
//
//     if (e.target.closest('.remove-btn')) {
//         const index = e.target.closest('.remove-btn').dataset.index;
//         cart.items.splice(index, 1);
//         localStorage.setItem('cart', JSON.stringify(cart));
//         renderCart();
//     }
// });
//
// document.addEventListener('change', (e) => {
//     if (e.target.classList.contains('quantity-input')) {
//         const index = e.target.dataset.index;
//         const newQuantity = parseInt(e.target.value);
//
//         if (newQuantity >= 1) {
//             cart.items[index].quantity = newQuantity;
//             localStorage.setItem('cart', JSON.stringify(cart));
//             renderCart();
//         } else {
//             e.target.value = cart.items[index].quantity;
//         }
//     }
// });
//
// document.getElementById('confirm-order').addEventListener('click', async () => {
//     if (cart.items.length === 0) {
//         alert('Корзина пуста!');
//         return;
//     }
//
//     const calculatedOnFrontTotal = parseFloat(
//         cart.items.reduce((sum, item) => {
//             return sum + item.quantity * item.price;
//         }, 0).toFixed(2)
//     );
//
//     const orderData = {
//         orderRowSet: cart.items.map(item => ({
//             applianceId: item.productId,
//             number: item.quantity
//         })),
//         calculatedOnFrontTotal
//     };
//
//     try {
//         const response = await fetch('/cart', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(orderData)
//         });
//
//         if (response.ok) {
//             localStorage.setItem("success-time-stamp", Date.now());
//             localStorage.removeItem('cart');
//             window.location.href = '/appliances?success=true';
//         } else {
//             alert(`${error_title}`);
//         }
//     } catch (error) {
//         alert(`${error_title}`);
//     }
// });
//
// renderCart();