package nl.fontys.kassasysteem.kassa_systeem_backend.controller;

import nl.fontys.kassasysteem.kassa_systeem_backend.dto.ProductDto;
import nl.fontys.kassasysteem.kassa_systeem_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ProductWebSocketController {

    private final ProductService productService;

    @Autowired
    public ProductWebSocketController(ProductService productService) {
        this.productService = productService;
    }

    @MessageMapping("/product/add")
    @SendTo("/topic/products")
    public ProductDto broadcastAndSaveProduct(ProductDto productDto) {
        ProductDto savedProduct = productService.save(productDto);
        // Return het opgeslagen product (met id, datum, etc.) naar alle clients
        return savedProduct;
    }
}
