import sys
import re

path = r'c:\Users\felip\OneDrive\UNIVERSIDAD\INGENIERIA DE SOFTWARE\reto-microservicios\reto-microservicios\mi-frontend\src\app\app.html'

with open(path, 'r', encoding='utf8') as f:
    content = f.read()

pattern = re.compile(r'<div class="order-meta">\s*<span>Prod ID: \{\{ order\.productId \}\}<\/span>\s*<span>Cant: \{\{ order\.quantity \}\}<\/span>\s*<\/div>')

replacement = '''<div class="order-meta" style="display: flex; flex-direction: column; gap: 4px;">
  <div *ngFor="let item of order.items" style="display: flex; justify-content: space-between; font-size: 0.9em; color: var(--text-muted);">
    <span>{{ getProductName(item.productId) }} (x{{ item.quantity }})</span>
    <span>${{ item.price }} c/u</span>
  </div>
</div>'''

new_content, count = pattern.subn(replacement, content)

print(f"Replaced {count} times.")

if count > 0:
    with open(path, 'w', encoding='utf8') as f:
        f.write(new_content)
