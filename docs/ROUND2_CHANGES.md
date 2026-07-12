# Round 2 Changes

## Frontend

- Added `/checkout` for both cart checkout and direct purchase.
- Added `/payment/:orderNo` with countdown, payment-window handling and status polling.
- Product cards and product details now support direct purchase.
- Redesigned cart with selection, stock validation, quantity updates and order summary.
- Redesigned order list with status tabs, pagination, payment and cancellation actions.
- Redesigned order details and payment result pages.
- Added shared order status presentation helpers and responsive layouts.
- Split frontend vendor bundles into Vue, Element Plus and Axios chunks.

## Backend

- Unified `CART` and `BUY_NOW` order creation while continuing to calculate all prices server-side.
- Added order source, recipient name, recipient phone and shipping address.
- Added direct-purchase item aggregation and quantity validation.
- Strengthened cart quantity checks against existing cart quantities and current stock.
- Strengthened order cancellation and payment-state validation.
- Added server-calculated remaining payment seconds for a timezone-independent countdown.
- Improved mock payment success page with parent-window notification.

## Database

- Added `V3__order_checkout_fields.sql`.
