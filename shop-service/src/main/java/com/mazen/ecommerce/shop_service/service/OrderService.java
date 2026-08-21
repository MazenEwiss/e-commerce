package com.mazen.ecommerce.shop_service.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mazen.ecommerce.shop_service.dto.OrderItemRequestDto;
import com.mazen.ecommerce.shop_service.dto.OrderItemResponseDto;
import com.mazen.ecommerce.shop_service.dto.OrderRequestDto;
import com.mazen.ecommerce.shop_service.dto.OrderResponseDto;
import com.mazen.ecommerce.shop_service.exception.OrderNotFoundException;
import com.mazen.ecommerce.shop_service.model.Order;
import com.mazen.ecommerce.shop_service.model.OrderItem;
import com.mazen.ecommerce.shop_service.model.OrderStatus;
import com.mazen.ecommerce.shop_service.model.Payment;
import com.mazen.ecommerce.shop_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    private OrderItem toOrderItem(OrderItemRequestDto dto, Order order) {
        OrderItem item;
        item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPriceAtPurchase(dto.getPriceAtPurchase());
        item.setOrder(order);
        return item;
    }

    private OrderResponseDto toOrderResponseDto(Order saved) {

        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setOrderId(saved.getId());
        responseDto.setUserId(saved.getUserId());
        responseDto.setStatus(saved.getStatus());
        responseDto.setOrderDate(saved.getOrderDate());
        responseDto.setTotalPrice(saved.getTotalPrice());

        List<OrderItemResponseDto> orderItemDtos = saved.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponseDto itemDto = new OrderItemResponseDto();
                    itemDto.setProductId(item.getProductId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                    return itemDto;
                })
                .toList();
        responseDto.setOrderItems(orderItemDtos);
        return responseDto;
    }

    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        Order order = new Order();
        order.setUserId(requestDto.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(new Date());

        // TODO 1: build a List<OrderItem> by calling toOrderItem(dto, order) for each
        //         item in requestDto.getOrderItems()
        List<OrderItem> orderItems = requestDto.getOrderItems().stream()
                .map(dto -> toOrderItem(dto, order))
                .toList();
        // TODO 2: calculate totalPrice by summing priceAtPurchase * quantity across
        //         that list (BigDecimal — use .multiply() and .add(), not +/*)
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);
        Payment payment = paymentService.processPayment(saved);
        saved.setPayment(payment);
        Order finalSaved = orderRepository.save(saved);

        // TODO 3: build the OrderResponseDto from `saved` (all top-level fields)
        OrderResponseDto responseDto = toOrderResponseDto(finalSaved);
        
        return responseDto;
    }
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return toOrderResponseDto(order);
    }
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::toOrderResponseDto)
                .toList();
    }
}
