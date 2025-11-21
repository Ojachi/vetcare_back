# VetCare Backend - API Endpoints Documentation

## Tabla de Contenidos
- [Autenticación](#autenticación)
- [Usuarios](#usuarios)
- [Administración](#administración)
- [Contraseñas](#contraseñas)
- [Mascotas](#mascotas)
- [Citas](#citas)
- [Diagnósticos](#diagnósticos)
- [Servicios](#servicios)
- [Productos](#productos)
- [Categorías](#categorías)
- [Carrito](#carrito)
- [Compras](#compras)
- [Chat IA](#chat-ia)

---

## Roles del Sistema
- **ADMIN**: Administrador con acceso total
- **EMPLOYEE**: Empleado con acceso operativo
- **VETERINARIAN**: Veterinario
- **OWNER**: Cliente/Dueño de mascotas

---

## Autenticación

### POST `/api/auth/login`
**Descripción**: Login de usuario (manejado por Spring Security Form Login)
- **Permisos**: Público
- **Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **Response**: `UserResponseDTO`
- **Flujo**:
  1. Spring Security valida credenciales
  2. Crea sesión con cookie JSESSIONID
  3. Retorna datos del usuario autenticado

### POST `/api/auth/logout`
**Descripción**: Cerrar sesión
- **Permisos**: Público
- **Response**: `{ "message": "Logout successful" }`

---

## Usuarios

### POST `/api/users/register`
**Descripción**: Registrar nuevo usuario (OWNER por defecto)
- **Permisos**: Público
- **Body**: `UserRegisterDTO`
```json
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "phone": "123456789",
  "address": "Calle 123"
}
```
- **Response**: `UserResponseDTO`
- **Flujo**:
  1. Valida que el email no exista
  2. Encripta la contraseña
  3. Asigna rol OWNER por defecto
  4. Crea usuario activo

### GET `/api/users/me`
**Descripción**: Obtener datos del usuario autenticado
- **Permisos**: Autenticado
- **Response**: `UserResponseDTO`

### PUT `/api/users/{id}`
**Descripción**: Actualizar datos de usuario
- **Permisos**: 
  - Usuario puede actualizar sus propios datos
  - ADMIN puede actualizar cualquier usuario
- **Body**: `UserUpdateDTO`
- **Response**: `UserResponseDTO`

### GET `/api/users`
**Descripción**: Listar todos los usuarios
- **Permisos**: ADMIN, EMPLOYEE
- **Response**: `List<UserResponseDTO>`

### GET `/api/users/professionals`
**Descripción**: Listar profesionales (VETERINARIAN, EMPLOYEE)
- **Permisos**: Autenticado
- **Response**: `List<UserResponseDTO>`

---

## Administración

### PUT `/api/admin/users/role`
**Descripción**: Cambiar rol de un usuario
- **Permisos**: ADMIN
- **Body**: `ChangeRoleDTO`
```json
{
  "userId": 1,
  "newRole": "EMPLOYEE"
}
```
- **Response**: `"Role changed"`

### PUT `/api/admin/users/activate`
**Descripción**: Activar usuario
- **Permisos**: ADMIN
- **Body**: `ActivateUserDTO`
- **Response**: `"User activated successfully"`

### PUT `/api/admin/users/deactivate`
**Descripción**: Desactivar usuario
- **Permisos**: ADMIN
- **Body**: `DeactivateUserDTO`
- **Response**: `"User deactivated"`

### GET `/api/admin/users`
**Descripción**: Listar todos los usuarios (admin)
- **Permisos**: ADMIN
- **Response**: `List<UserResponseDTO>`

### DELETE `/api/admin/users/{id}`
**Descripción**: Eliminar usuario (soft delete)
- **Permisos**: ADMIN
- **Response**: `"User deleted"`

---

## Contraseñas

### POST `/api/auth/forgot-password`
**Descripción**: Solicitar recuperación de contraseña (Paso 1)
- **Permisos**: Público
- **Body**: `ForgotPasswordDTO`
```json
{
  "email": "user@example.com"
}
```
- **Response**: `"OTP sent to your email successfully"`
- **Flujo**:
  1. Genera OTP de 6 dígitos
  2. Guarda en BD con expiración (10 min)
  3. Envía email con OTP

### POST `/api/auth/verify-otp`
**Descripción**: Verificar OTP (Paso 2)
- **Permisos**: Público
- **Body**: `VerifyOtpDTO`
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```
- **Response**: `"OTP verified successfully"`
- **Flujo**:
  1. Valida que OTP no haya expirado
  2. Valida que OTP sea correcto
  3. Marca OTP como verificado

### POST `/api/auth/reset-password`
**Descripción**: Resetear contraseña con OTP (Paso 3)
- **Permisos**: Público
- **Body**: `ResetPasswordDTO`
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "newPassword123"
}
```
- **Response**: `"Password reset successfully"`
- **Flujo**:
  1. Valida OTP verificado
  2. Actualiza contraseña
  3. Elimina token OTP

### PUT `/api/users/change-password`
**Descripción**: Cambiar contraseña (usuario autenticado)
- **Permisos**: Autenticado
- **Body**: `ChangePasswordAuthenticatedDTO`
```json
{
  "currentPassword": "oldPassword",
  "newPassword": "newPassword123"
}
```
- **Response**: `"Password changed successfully"`

---

## Mascotas

### POST `/api/pets`
**Descripción**: Crear mascota
- **Permisos**: 
  - OWNER: Solo puede crear sus propias mascotas
  - ADMIN, EMPLOYEE, VETERINARIAN: Pueden crear para cualquier cliente
- **Body**: `PetDTO`
```json
{
  "name": "Firulais",
  "species": "Perro",
  "breed": "Labrador",
  "age": 3,
  "weight": 25.5,
  "sex": "Macho",
  "ownerId": 1
}
```
- **Response**: `PetResponseDTO`

### PUT `/api/pets/{id}`
**Descripción**: Actualizar mascota
- **Permisos**: 
  - OWNER: Solo sus mascotas
  - ADMIN, EMPLOYEE: Cualquier mascota
- **Body**: `PetDTO`
- **Response**: `PetResponseDTO`

### PUT `/api/pets/{id}/activate`
**Descripción**: Activar mascota
- **Permisos**: ADMIN, EMPLOYEE, OWNER (solo suyas)
- **Response**: 204 No Content

### PUT `/api/pets/{id}/deactivate`
**Descripción**: Desactivar mascota
- **Permisos**: ADMIN, EMPLOYEE, OWNER (solo suyas)
- **Response**: 204 No Content

### DELETE `/api/pets/{id}`
**Descripción**: Eliminar mascota (soft delete)
- **Permisos**: ADMIN, EMPLOYEE, VETERINARIAN, OWNER (solo suyas)
- **Response**: 204 No Content
- **Validaciones**:
  - No puede tener citas asociadas
  - No puede tener diagnósticos asociados
- **Errores**:
  - `"Cannot delete pet with existing appointments. Found X appointment(s)."`
  - `"Cannot delete pet with existing diagnoses. Found X diagnosis(es)."`

### GET `/api/pets/{id}`
**Descripción**: Obtener mascota por ID
- **Permisos**: 
  - OWNER: Solo sus mascotas
  - ADMIN, EMPLOYEE, VETERINARIAN: Cualquier mascota
- **Response**: `PetResponseDTO`

### GET `/api/pets`
**Descripción**: Listar mascotas
- **Permisos**: 
  - OWNER: Solo sus mascotas
  - ADMIN, EMPLOYEE, VETERINARIAN: Todas las mascotas
- **Response**: `List<PetResponseDTO>`

### GET `/api/pets/owner/{ownerId}`
**Descripción**: Listar mascotas por dueño
- **Permisos**: 
  - OWNER: Solo si es su propio ID
  - ADMIN, EMPLOYEE, VETERINARIAN: Cualquier dueño
- **Response**: `List<PetResponseDTO>`

---

## Citas

### POST `/api/appointments`
**Descripción**: Crear cita
- **Permisos**: 
  - OWNER: Solo para sus mascotas
  - ADMIN, EMPLOYEE: Para cualquier cliente
- **Body**: `AppointmentDTO`
```json
{
  "petId": 1,
  "serviceId": 1,
  "startDateTime": "2024-01-15T10:00:00",
  "assignedToId": 2,
  "note": "Vacunación anual"
}
```
- **Response**: `AppointmentResponseDTO`
- **Flujo**:
  1. Valida que el servicio esté activo
  2. Valida permisos del usuario
  3. Asigna profesional (manual o automático)
  4. Valida disponibilidad del profesional
  5. Crea cita en estado PENDING

### PUT `/api/appointments/{id}`
**Descripción**: Actualizar cita
- **Permisos**: 
  - OWNER: Solo sus citas
  - ADMIN, EMPLOYEE: Cualquier cita
- **Body**: `AppointmentDTO`
- **Response**: `AppointmentResponseDTO`

### PUT `/api/appointments/{id}/cancel`
**Descripción**: Cancelar cita
- **Permisos**: 
  - OWNER: Solo sus citas
  - ADMIN, EMPLOYEE, Profesional asignado: Cualquier cita
- **Response**: 204 No Content
- **Validación**: Solo citas PENDING pueden cancelarse

### PUT `/api/appointments/{id}/status`
**Descripción**: Cambiar estado de cita
- **Permisos**: Profesional asignado, ADMIN, EMPLOYEE
- **Body**: `ChangeAppointmentStatusDTO`
```json
{
  "status": "COMPLETED"
}
```
- **Response**: 204 No Content
- **Transiciones válidas**:
  - PENDING → ACCEPTED o CANCELLED
  - ACCEPTED → COMPLETED o CANCELLED
- **Validación**: Servicios veterinarios requieren diagnóstico antes de COMPLETED

### GET `/api/appointments/{id}`
**Descripción**: Obtener cita por ID
- **Permisos**: 
  - OWNER: Solo sus citas
  - ADMIN, EMPLOYEE, Profesional asignado: Cualquier cita
- **Response**: `AppointmentResponseDTO`

### GET `/api/appointments`
**Descripción**: Listar citas
- **Permisos**: 
  - OWNER: Solo sus citas
  - VETERINARIAN: Citas asignadas a él
  - ADMIN, EMPLOYEE: Todas las citas
- **Response**: `List<AppointmentResponseDTO>`

### GET `/api/appointments/admin`
**Descripción**: Listar citas con filtros
- **Permisos**: ADMIN, EMPLOYEE
- **Query Params**:
  - `ownerId`: Filtrar por dueño
  - `petId`: Filtrar por mascota
  - `serviceId`: Filtrar por servicio
  - `assignedToId`: Filtrar por profesional
  - `startDate`: Fecha inicio
  - `endDate`: Fecha fin
- **Response**: `List<AppointmentResponseDTO>`

### GET `/api/appointments/available-professionals`
**Descripción**: Obtener profesionales disponibles
- **Permisos**: Autenticado
- **Query Params**:
  - `serviceId`: ID del servicio
  - `dateTime`: Fecha y hora deseada
- **Response**: `List<AvailableProfessionalDTO>`
- **Flujo**:
  1. Obtiene profesionales según tipo de servicio
  2. Verifica disponibilidad en la fecha/hora
  3. Retorna lista con disponibilidad y próximo slot

---

## Diagnósticos

### POST `/api/diagnoses`
**Descripción**: Crear diagnóstico
- **Permisos**: VETERINARIAN, ADMIN
- **Body**: `DiagnosisDTO`
```json
{
  "appointmentId": 1,
  "diagnosis": "Infección respiratoria leve",
  "treatment": "Antibióticos por 7 días",
  "observations": "Control en 1 semana",
  "date": "2024-01-15"
}
```
- **Response**: `DiagnosisResponseDTO`
- **Validaciones**:
  - Cita debe estar ACCEPTED o COMPLETED
  - Cita no debe tener diagnóstico previo

### PUT `/api/diagnoses/{id}`
**Descripción**: Actualizar diagnóstico
- **Permisos**: Veterinario creador, ADMIN
- **Body**: `DiagnosisDTO`
- **Response**: `DiagnosisResponseDTO`

### PUT `/api/diagnoses/{id}/activate`
**Descripción**: Activar diagnóstico
- **Permisos**: Veterinario creador, ADMIN
- **Response**: 204 No Content

### PUT `/api/diagnoses/{id}/deactivate`
**Descripción**: Desactivar diagnóstico
- **Permisos**: Veterinario creador, ADMIN
- **Response**: 204 No Content

### GET `/api/diagnoses/{id}`
**Descripción**: Obtener diagnóstico por ID
- **Permisos**: 
  - OWNER: Solo de sus mascotas
  - VETERINARIAN, EMPLOYEE, ADMIN: Cualquier diagnóstico
- **Response**: `DiagnosisResponseDTO`

### GET `/api/diagnoses`
**Descripción**: Listar diagnósticos
- **Permisos**: 
  - OWNER: Solo de sus mascotas
  - VETERINARIAN, EMPLOYEE, ADMIN: Todos
- **Response**: `List<DiagnosisResponseDTO>`

### GET `/api/diagnoses/admin`
**Descripción**: Listar diagnósticos con filtros
- **Permisos**: ADMIN
- **Query Params**:
  - `petId`: Filtrar por mascota
  - `vetId`: Filtrar por veterinario
  - `startDate`: Fecha inicio
  - `endDate`: Fecha fin
- **Response**: `List<DiagnosisResponseDTO>`

### GET `/api/diagnoses/pet/{petId}`
**Descripción**: Listar diagnósticos de una mascota
- **Permisos**: 
  - OWNER: Solo si es su mascota
  - VETERINARIAN, EMPLOYEE, ADMIN: Cualquier mascota
- **Response**: `List<DiagnosisResponseDTO>`

### GET `/api/diagnoses/my-diagnoses`
**Descripción**: Listar diagnósticos creados por el veterinario
- **Permisos**: VETERINARIAN
- **Response**: `List<DiagnosisResponseDTO>`

### GET `/api/diagnoses/{id}/pdf`
**Descripción**: Descargar diagnóstico en PDF
- **Permisos**: 
  - OWNER: Solo de sus mascotas
  - VETERINARIAN, EMPLOYEE, ADMIN: Cualquier diagnóstico
- **Response**: PDF file

---

## Servicios

### POST `/api/admin/services`
**Descripción**: Crear servicio
- **Permisos**: ADMIN
- **Body**: `ServiceDTO`
```json
{
  "name": "Consulta General",
  "description": "Consulta veterinaria general",
  "price": 50.00,
  "durationMinutes": 30,
  "requiresVeterinarian": true
}
```
- **Response**: `ServiceResponseDTO`

### PUT `/api/admin/services/{id}`
**Descripción**: Actualizar servicio
- **Permisos**: ADMIN
- **Body**: `ServiceDTO`
- **Response**: `ServiceResponseDTO`

### DELETE `/api/admin/services/{id}`
**Descripción**: Eliminar servicio (soft delete)
- **Permisos**: ADMIN
- **Response**: 204 No Content

### PUT `/api/admin/services/{id}/activate`
**Descripción**: Activar servicio
- **Permisos**: ADMIN
- **Response**: 204 No Content

### PUT `/api/admin/services/{id}/deactivate`
**Descripción**: Desactivar servicio
- **Permisos**: ADMIN
- **Response**: 204 No Content

### GET `/api/services/{id}`
**Descripción**: Obtener servicio por ID
- **Permisos**: Público
- **Response**: `ServiceResponseDTO`

### GET `/api/services`
**Descripción**: Listar todos los servicios activos
- **Permisos**: Público
- **Response**: `List<ServiceResponseDTO>`

---

## Productos

### POST `/api/products`
**Descripción**: Crear producto
- **Permisos**: ADMIN
- **Body**: `ProductDTO`
```json
{
  "name": "Alimento Premium",
  "description": "Alimento balanceado para perros adultos",
  "price": 45.99,
  "image": "data:image/png;base64,...",
  "stock": 100,
  "categoryId": 1
}
```
- **Response**: `ProductResponseDTO`
- **Validaciones**:
  - Imagen debe ser Base64 válido (PNG/JPEG)
  - Categoría debe estar activa

### PUT `/api/products/{id}`
**Descripción**: Actualizar producto
- **Permisos**: ADMIN
- **Body**: `ProductDTO`
- **Response**: `ProductResponseDTO`

### PUT `/api/products/{id}/activate`
**Descripción**: Activar producto
- **Permisos**: ADMIN
- **Response**: 204 No Content

### DELETE `/api/products/{id}`
**Descripción**: Eliminar producto (soft delete)
- **Permisos**: ADMIN
- **Response**: 204 No Content
- **Nota**: Productos en carritos no se eliminan, se validan al comprar

### GET `/api/products/{id}`
**Descripción**: Obtener producto por ID
- **Permisos**: Público
- **Response**: `ProductResponseDTO`

### GET `/api/products`
**Descripción**: Listar productos
- **Permisos**: Público
- **Query Params**:
  - `categoryId`: Filtrar por categoría
- **Response**: `List<ProductResponseDTO>`

---

## Categorías

### POST `/api/categories`
**Descripción**: Crear categoría
- **Permisos**: ADMIN
- **Body**: `CategoryDTO`
```json
{
  "name": "Alimentos",
  "description": "Alimentos para mascotas"
}
```
- **Response**: `CategoryResponseDTO`

### PUT `/api/categories/{id}`
**Descripción**: Actualizar categoría
- **Permisos**: ADMIN
- **Body**: `CategoryDTO`
- **Response**: `CategoryResponseDTO`

### DELETE `/api/categories/{id}`
**Descripción**: Eliminar categoría (soft delete)
- **Permisos**: ADMIN
- **Response**: 204 No Content
- **Flujo**:
  1. Desvincula todos los productos activos (category = null)
  2. Marca categoría como inactiva

### GET `/api/categories/{id}`
**Descripción**: Obtener categoría por ID
- **Permisos**: Público
- **Response**: `CategoryResponseDTO`

### GET `/api/categories`
**Descripción**: Listar categorías activas
- **Permisos**: Público
- **Response**: `List<CategoryResponseDTO>`

---

## Carrito

### POST `/api/cart/add`
**Descripción**: Agregar producto al carrito
- **Permisos**: Autenticado
- **Body**: `AddToCartDTO`
```json
{
  "productId": 1,
  "quantity": 2
}
```
- **Response**: `CartResponseDTO`
- **Validaciones**:
  - Producto debe tener stock suficiente
  - Si el producto ya existe, suma cantidades

### PUT `/api/cart/item/{itemId}`
**Descripción**: Actualizar cantidad de item
- **Permisos**: Autenticado (solo su carrito)
- **Body**: `UpdateCartItemDTO`
```json
{
  "quantity": 3
}
```
- **Response**: `CartResponseDTO`
- **Nota**: Si quantity <= 0, elimina el item

### DELETE `/api/cart/item/{itemId}`
**Descripción**: Eliminar item del carrito
- **Permisos**: Autenticado (solo su carrito)
- **Response**: `CartResponseDTO`

### GET `/api/cart`
**Descripción**: Obtener carrito del usuario
- **Permisos**: Autenticado
- **Response**: `CartResponseDTO`

### DELETE `/api/cart/clear`
**Descripción**: Vaciar carrito
- **Permisos**: Autenticado
- **Response**: 200 OK

---

## Compras

### POST `/api/purchases/buy-now`
**Descripción**: Comprar producto directamente
- **Permisos**: Autenticado
- **Body**: `BuyNowDTO`
```json
{
  "productId": 1,
  "quantity": 2
}
```
- **Response**: `PurchaseResponseDTO`
- **Flujo**:
  1. Valida producto activo
  2. Valida stock suficiente
  3. Crea compra en estado PENDING
  4. Reduce stock
  5. Invalida caché de estadísticas

### POST `/api/purchases/from-cart`
**Descripción**: Comprar desde el carrito
- **Permisos**: Autenticado
- **Response**: `PurchaseResponseDTO`
- **Flujo**:
  1. Valida carrito no vacío
  2. Valida todos los productos activos
  3. Valida stock de todos los productos
  4. Crea compra en estado PENDING
  5. Reduce stock de todos los productos
  6. Limpia el carrito
  7. Invalida caché de estadísticas

### POST `/api/purchases/manual`
**Descripción**: Registrar venta manual
- **Permisos**: ADMIN, EMPLOYEE
- **Body**: `ManualPurchaseDTO`
```json
{
  "userId": 1,
  "notes": "Venta en mostrador",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```
- **Response**: `PurchaseResponseDTO`
- **Validaciones**:
  - Usuario debe ser OWNER
  - Productos deben estar activos
  - Stock suficiente
- **Flujo**:
  1. Valida usuario y productos
  2. Crea compra en estado COMPLETED
  3. Reduce stock
  4. Invalida caché de estadísticas

### PUT `/api/purchases/{purchaseId}/complete`
**Descripción**: Completar compra
- **Permisos**: ADMIN, EMPLOYEE
- **Response**: `PurchaseResponseDTO`
- **Flujo**:
  1. Cambia estado a COMPLETED
  2. Invalida caché de estadísticas

### PUT `/api/purchases/{purchaseId}/cancel`
**Descripción**: Cancelar compra
- **Permisos**: ADMIN, EMPLOYEE
- **Response**: `PurchaseResponseDTO`
- **Validaciones**:
  - No se puede cancelar compra COMPLETED
- **Flujo**:
  1. Restaura stock de todos los productos
  2. Cambia estado a CANCELLED
  3. Invalida caché de estadísticas

### GET `/api/purchases/{purchaseId}`
**Descripción**: Obtener compra por ID
- **Permisos**: Usuario dueño de la compra
- **Response**: `PurchaseResponseDTO`

### GET `/api/purchases`
**Descripción**: Listar compras del usuario
- **Permisos**: Autenticado
- **Query Params**: Paginación (Pageable)
- **Response**: `Page<PurchaseResponseDTO>`

### GET `/api/purchases/all`
**Descripción**: Listar todas las compras con filtros
- **Permisos**: ADMIN, EMPLOYEE
- **Query Params**:
  - `status`: Filtrar por estado (PENDING, COMPLETED, CANCELLED)
  - `userId`: Filtrar por usuario
  - `from`: Fecha desde
  - `to`: Fecha hasta
  - Paginación (Pageable)
- **Response**: `Page<PurchaseResponseDTO>`

### GET `/api/purchases/statistics`
**Descripción**: Obtener estadísticas de ventas
- **Permisos**: ADMIN, EMPLOYEE
- **Query Params**:
  - `period`: LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, CURRENT_MONTH, CURRENT_YEAR, CUSTOM
  - `from`: Fecha desde (para CUSTOM)
  - `to`: Fecha hasta (para CUSTOM)
- **Response**: `StatisticsDTO`
```json
{
  "period": "LAST_30_DAYS",
  "dateFrom": "2024-01-01T00:00:00",
  "dateTo": "2024-01-31T23:59:59",
  "totalAmount": 15000.00,
  "totalOrders": 150,
  "averageOrderValue": 100.00,
  "salesByStatus": {
    "COMPLETED": 120,
    "PENDING": 20,
    "CANCELLED": 10
  },
  "topSellingProducts": [...],
  "salesByCategory": [...]
}
```
- **Nota**: Resultados en caché, se invalida al crear/completar/cancelar compras

---

## Chat IA

### POST `/api/chat/consult`
**Descripción**: Consultar al asistente veterinario IA
- **Permisos**: Público
- **Body**: `ChatRequestDTO`
```json
{
  "message": "Mi perro tiene tos, ¿qué puede ser?"
}
```
- **Response**: `ChatResponseDTO`
- **Límite**: Rate limiting aplicado por IP/usuario

### GET `/api/chat/status`
**Descripción**: Verificar estado del servicio de chat
- **Permisos**: Público
- **Response**:
```json
{
  "status": "active",
  "service": "Veterinary AI Assistant",
  "version": "1.0"
}
```

---

## Códigos de Estado HTTP

- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Operación exitosa sin contenido de respuesta
- **400 Bad Request**: Datos inválidos o validación fallida
- **401 Unauthorized**: No autenticado
- **403 Forbidden**: No autorizado (sin permisos)
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto (ej: email duplicado)
- **500 Internal Server Error**: Error del servidor

---

## Notas Importantes

### Autenticación
- El sistema usa **Spring Security Form Login** con sesiones
- Las sesiones se mantienen con cookies `JSESSIONID`
- CORS configurado para permitir credenciales

### Soft Deletes
- Usuarios, mascotas, productos, categorías, servicios y diagnósticos usan **soft delete**
- Se marca `active = false` en lugar de eliminar físicamente
- Los registros inactivos no aparecen en listados públicos

### Validaciones de Stock
- Al agregar al carrito: valida stock disponible
- Al comprar: valida stock y productos activos
- Al cancelar compra: restaura stock

### Caché
- Las estadísticas de ventas están en caché
- Se invalida automáticamente al crear/completar/cancelar compras

### Transacciones
- Operaciones de compra usan `@Transactional`
- Si falla algún paso, se hace rollback completo

---

## Ejemplos de Flujos Completos

### Flujo: Registro y Primera Compra
1. `POST /api/users/register` - Registrar usuario
2. `POST /api/auth/login` - Iniciar sesión
3. `GET /api/products` - Ver productos
4. `POST /api/cart/add` - Agregar al carrito
5. `GET /api/cart` - Ver carrito
6. `POST /api/purchases/from-cart` - Comprar
7. `GET /api/purchases` - Ver mis compras

### Flujo: Agendar Cita
1. `POST /api/auth/login` - Iniciar sesión
2. `GET /api/pets` - Ver mis mascotas
3. `GET /api/services` - Ver servicios disponibles
4. `GET /api/appointments/available-professionals` - Ver profesionales disponibles
5. `POST /api/appointments` - Crear cita
6. `GET /api/appointments` - Ver mis citas

### Flujo: Recuperar Contraseña
1. `POST /api/auth/forgot-password` - Solicitar OTP
2. (Usuario recibe email con OTP)
3. `POST /api/auth/verify-otp` - Verificar OTP
4. `POST /api/auth/reset-password` - Cambiar contraseña
5. `POST /api/auth/login` - Iniciar sesión con nueva contraseña

---

**Versión**: 1.0  
**Última actualización**: 2024-01-21
