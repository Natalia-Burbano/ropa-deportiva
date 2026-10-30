# Viveros Ropa Deportiva – Agregar pedido de cliente

MVP del proyecto grupal de Ingeniería de Software II.
Aplicación de escritorio en **Java 25 + JavaFX**. Los datos se guardan en archivos **JSON**.

- **Pantalla 1 – Agregar pedido** (transacción): buscar cliente, uniformes, cotización, abono, diseño, fecha de entrega y confirmar.
- **Pantalla 2 – Clientes** (CRUD): listar, crear, editar y eliminar clientes.

## Cómo correrlo

1. Abrir la carpeta del proyecto en IntelliJ (*File → Open*). IntelliJ reconoce el `pom.xml` y descarga JavaFX y Gson solo.
2. Revisar que el proyecto use el JDK 25 (*File → Project Structure → SDK*).
3. Correr la clase `co.edu.uniquindio.viveros.Launcher` con el botón Run.

También se puede correr desde la terminal con `mvn javafx:run`.

> No se debe correr `App` directamente, porque sale el error "faltan los componentes de JavaFX". Por eso existe `Launcher`.

## Datos de prueba

La primera vez que se abre la app se crea la carpeta `data/` con:

| Archivo | Contenido |
|---|---|
| `clientes.json` | 12 clientes |
| `pedidos.json` | 20 pedidos: 5 sin confirmar, 5 confirmados, 5 en producción y 5 entregados |
| `encargados.json` | 3 encargados |
| `disenadores.json` | 2 diseñadores |
| `disciplinas.json` | Fútbol, Microfútbol, Baloncesto y Voleibol |

Para volver a los datos iniciales, se borra la carpeta `data/` y se abre la app otra vez.
Esa carpeta no se sube al repositorio.

## Usuarios demo

| Encargado | Cargo |
|---|---|
| Marcela Viveros | Encargada |
| Carlos Muñoz | Vendedor |
| Paola Rosero | Vendedora |

## Estructura del código

```
src/main/java/co/edu/uniquindio/viveros/
├── App.java / Launcher.java   Ventana principal con las 2 pestañas
├── modelo/                    Clases del diagrama de clases (mismos nombres de atributos)
├── controlador/               Lógica de cada pantalla (hace el papel de los endpoints)
│   ├── PedidoControlador      Transacción Agregar pedido   → Integrante 2
│   └── ClienteControlador     CRUD de Cliente              → Integrante 1
├── vista/                     Pantallas JavaFX
├── persistencia/              Guardado en JSON y datos de prueba
└── util/Fechas.java           Cálculo de días hábiles (Regla 002)
```

## Cómo probar la pantalla Agregar pedido

1. Escoger el encargado y buscar un cliente (por ejemplo `cabrera`).
2. Llenar el nombre del equipo, el tipo de prenda y el valor unitario.
3. Agregar los uniformes con disciplina, nombre, número y talla.
4. Marcar «El cliente aceptó la cotización».
5. Cargar el diseño (una imagen JPG o PNG) y marcarlo como aprobado.
6. Escribir el abono (mínimo el 50%) y el método de pago.
7. Dar **Confirmar pedido**: sale el comprobante y desde ahí se puede enviar a producción.

Para ver un error bloqueante: escribir un abono menor al 50% y dar Confirmar.

## Reglas de negocio y dónde se validan

| Regla | Dónde |
|---|---|
| Abono mínimo del 50% del valor total | `PedidoControlador.validarReglas()` y formulario |
| Fecha de entrega = fecha del abono + 10 días hábiles | `PedidoControlador.calcularFechaEntrega()` |
| Cada uniforme con disciplina, nombre, número y talla | `PedidoControlador.validarReglas()` y formulario |
| Sin diseño aprobado no pasa a producción | `PedidoControlador.enviarAProduccion()` |
| Cotización aceptada antes del abono | `PedidoControlador.validarReglas()` |
| Todo pedido con un encargado responsable | `PedidoControlador.validarReglas()` |
| Cliente sin registros duplicados | `ClienteControlador.crear()` |

## Ramas

- `main`: versión que funciona.
- `feature/agregar-pedido`: pantalla 1.
- `feature/crud-cliente`: pantalla 2.

Cada una trabaja en su rama y une los cambios a `main` con PRs pequeños.
