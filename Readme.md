
## ERD

```mermaid
erDiagram
    USER ||--|| WALLET : owns
    WALLET ||--o{ TRANSACTION : has
    USER ||--|| CART : owns
    USER ||--o{ ORDER : places
    CART ||--o{ CART_ITEM : contains
    ORDER ||--o{ ORDER_ITEM : contains
    ORDER ||--|| PAYMENT : has
    CATEGORY ||--o{ PRODUCT : classifies

    USER {
        Long id PK
        string userName
        string email
        string firstName
        string lastName
        string password
    }
    WALLET {
        Long walletId PK
        Long user_id FK
        BigDecimal balance
    }
    TRANSACTION {
        Long transactionId PK
        Long wallet_id FK
        BigDecimal amount
        enum transactionType
        enum transactionState
        timestamp timestamp
    }
    CATEGORY {
        Long categoryId PK
        string categoryName
    }
    PRODUCT {
        Long id PK
        Long category_id FK
        string name
        BigDecimal price
        Integer quantity
        string imageUrl
        boolean isNew
    }
    CART {
        Long id PK
        Long userId
        BigDecimal totalPrice
    }
    CART_ITEM {
        Long cartItemId PK
        Long cart_id FK
        Long productId "references PRODUCT by ID only, cross-service"
        int quantity
        BigDecimal priceAtPurchase
    }
    ORDER {
        Long id PK
        Long userId
        BigDecimal totalPrice
        enum status
        Date orderDate
    }
    ORDER_ITEM {
        Long id PK
        Long order_id FK
        Long productId "references PRODUCT by ID only, cross-service"
        int quantity
        BigDecimal priceAtPurchase
    }
    PAYMENT {
        Long paymentId PK
        Long order_id FK
        BigDecimal amount
        enum paymentStatus
        Long walletTransactionId "references TRANSACTION by ID only, cross-service"
        timestamp paymentDate
    }
```

Note: `PRODUCT`, `CART_ITEM`/`ORDER_ITEM`, and `TRANSACTION`/`PAYMENT` links across the dashed service boundary (Inventory ↔ Shop ↔ Wallet) are **not real foreign keys** — each service has its own database, so these are ID references resolved at runtime via Feign calls, not DB-enforced relationships. That's also why gap #3 above (no FK guard) exists — there's no database constraint to catch it.
