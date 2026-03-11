import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AuthService } from './services/auth.service';
import { CatalogService, Product } from './services/catalog.service';
import { OrderService, OrderResponse } from './services/order.service';
import { CommonModule } from '@angular/common';

// An item in the shopping cart
export interface CartItem {
  product: Product;
  quantity: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  title = 'mi-frontend';

  // State
  isLoggedIn = false;
  isAdmin = false;
  isLoginView = true;
  products: Product[] = [];
  orders: OrderResponse[] = [];
  cart: CartItem[] = [];
  isProductFormOpen = false;
  editingProductId: number | null = null;
  customerName = '';

  // Forms
  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  registerForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(4)]),
    role: new FormControl('USER', [Validators.required]),
  });

  productForm = new FormGroup({
    name: new FormControl('', [Validators.required]),
    price: new FormControl(0, [Validators.required, Validators.min(0)]),
    description: new FormControl('', [Validators.required]),
    stock: new FormControl(0, [Validators.required, Validators.min(0)]),
  });

  constructor(
    private authService: AuthService,
    private catalogService: CatalogService,
    private orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.isAdmin = this.authService.getRole() === 'ROLE_ADMIN' || this.authService.getRole() === 'ADMIN';
      this.customerName = this.authService.getUsername();
      this.loadData();
    }
  }

  toggleAuthView(): void {
    this.isLoginView = !this.isLoginView;
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value as any).subscribe({
        next: (res: any) => {
          if (res.token) {
            this.authService.saveToken(res.token);
            this.isLoggedIn = true;
            this.isAdmin = this.authService.getRole() === 'ROLE_ADMIN' || this.authService.getRole() === 'ADMIN';
            this.customerName = this.authService.getUsername();
            this.loadData();
          }
        },
        error: (err: any) => {
          alert('Login fallido: Verifica que las credenciales sean correctas.');
        }
      });
    }
  }

  onRegister(): void {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value as any).subscribe({
        next: (res: any) => {
          alert('¡Registro Exitoso! Ahora puedes iniciar sesión.');
          this.toggleAuthView();
        },
        error: (err: any) => {
          alert('Registro fallido: ' + (err.error?.message || 'Error del servidor.'));
        }
      });
    }
  }

  onLogout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.products = [];
    this.orders = [];
    this.cart = [];
    this.closeProductForm();
  }

  loadData(): void {
    this.catalogService.getProducts().subscribe({
      next: (data: Product[]) => this.products = data,
      error: (e: any) => console.error(e)
    });

    this.orderService.getOrders().subscribe({
      next: (data: OrderResponse[]) => {
        if (this.isAdmin) {
          this.orders = data; // admin sees all
        } else {
          // regular user: filter by username (customerName from token)
          this.orders = data.filter(o => o.customerName === this.customerName);
        }
      },
      error: (e: any) => console.error(e)
    });
  }

  getProductName(productId: number): string {
    const product = this.products.find(p => p.id === productId);
    return product ? product.name : `Prod #${productId}`;
  }

  // --- CART ---

  addToCart(product: Product): void {
    const existing = this.cart.find(c => c.product.id === product.id);
    if (existing) {
      if (existing.quantity < product.stock) {
        existing.quantity++;
      } else {
        alert(`No hay más stock disponible de "${product.name}"`);
      }
    } else {
      if (product.stock > 0) {
        this.cart.push({ product, quantity: 1 });
      } else {
        alert(`"${product.name}" no tiene stock disponible.`);
      }
    }
  }

  decreaseCart(item: CartItem): void {
    if (item.quantity > 1) {
      item.quantity--;
    } else {
      this.removeFromCart(item);
    }
  }

  removeFromCart(item: CartItem): void {
    this.cart = this.cart.filter(c => c.product.id !== item.product.id);
  }

  getCartTotal(): number {
    return this.cart.reduce((sum, c) => sum + c.product.price * c.quantity, 0);
  }

  confirmOrder(): void {
    if (this.cart.length === 0) {
      alert('El carrito está vacío.');
      return;
    }
    if (!this.customerName.trim()) {
      alert('Por favor ingresa el nombre del cliente.');
      return;
    }

    const items = this.cart.map(item => ({
      productId: item.product.id,
      quantity: item.quantity
    }));

    const orderReq = {
      customerName: this.customerName,
      userId: 1,
      items: items
    };

    this.orderService.createOrder(orderReq).subscribe({
      next: () => {
        alert('¡Pedido realizado con éxito!');
        this.cart = [];
        this.loadData();
      },
      error: (err) => {
        const msg = err.error?.message || 'Error al procesar el pedido. Verifica el stock disponible.';
        alert(msg);
      }
    });
  }

  // --- ORDERS ---

  get completedOrders(): OrderResponse[] {
    return this.orders.filter(o => o.status === 'COMPLETED' || o.status === 'CONFIRMED');
  }

  get cancelledOrders(): OrderResponse[] {
    return this.orders.filter(o => o.status === 'CANCELLED');
  }

  get pendingOrders(): OrderResponse[] {
    return this.orders.filter(o => o.status !== 'COMPLETED' && o.status !== 'CONFIRMED' && o.status !== 'CANCELLED');
  }

  cancelOrder(id: number): void {
    this.orderService.cancelOrder(id).subscribe({
      next: (res: any) => {
        alert('Orden Cancelada Exitosamente.');
        this.loadData();
      },
      error: (e: any) => {
        alert('Error al cancelar la orden.');
        console.error(e);
      }
    });
  }

  // --- ADMIN PRODUCT CRUD ---

  openProductForm(product?: Product): void {
    this.isProductFormOpen = true;
    if (product) {
      this.editingProductId = product.id;
      this.productForm.patchValue({
        name: product.name,
        price: product.price,
        description: product.description,
        stock: product.stock
      });
    } else {
      this.editingProductId = null;
      this.productForm.reset();
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  closeProductForm(): void {
    this.isProductFormOpen = false;
    this.editingProductId = null;
    this.productForm.reset();
  }

  saveProduct(): void {
    if (this.productForm.valid) {
      const productData = this.productForm.value as Partial<Product>;
      if (this.editingProductId) {
        this.catalogService.updateProduct(this.editingProductId, productData).subscribe({
          next: () => {
            alert('Producto actualizado exitosamente');
            this.closeProductForm();
            this.loadData();
          },
          error: (err) => alert('Error al actualizar el producto.')
        });
      } else {
        this.catalogService.createProduct(productData).subscribe({
          next: () => {
            alert('Producto creado exitosamente');
            this.closeProductForm();
            this.loadData();
          },
          error: (err) => alert('Error al crear el producto.')
        });
      }
    }
  }

  deleteProduct(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
      this.catalogService.deleteProduct(id).subscribe({
        next: () => {
          alert('Producto eliminado exitosamente');
          this.loadData();
        },
        error: (err) => alert('Error al eliminar el producto.')
      });
    }
  }
}
