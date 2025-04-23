CREATE TYPE payment_status AS ENUM ('AUTHORIZED', 'DECLINED', 'REJECTED');
CREATE TYPE currency_code AS ENUM ('GBP', 'USD', 'EUR');
CREATE TABLE payment (
    id UUID PRIMARY KEY,
    order_id UUID UNIQUE,
    status payment_status NOT NULL,
    card_number VARCHAR(19) NOT NULL CHECK (
      char_length(card_number) BETWEEN 14 AND 19 AND card_number ~ '^[0-9]+$'
    ),
    expiry_date DATE NOT NULL CHECK (
         expiry_date > CURRENT_DATE
     ),
    currency_code currency_code NOT NULL,
    amount BIGINT NOT NULL,
    cvv VARCHAR(4) NOT NULL CHECK (
      char_length(cvv) BETWEEN 3 AND 4 AND cvv ~ '^[0-9]+$'
     )
);