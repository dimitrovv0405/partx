package com.example.partx.models.dtos;

import com.example.partx.models.dtos.order.CartItemDto;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartFormDto {
    private List<CartItemDto> items = new ArrayList<>();
}
