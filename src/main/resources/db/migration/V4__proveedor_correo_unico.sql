ALTER TABLE proveedor
    ADD CONSTRAINT uk_proveedor_correo UNIQUE (correo);