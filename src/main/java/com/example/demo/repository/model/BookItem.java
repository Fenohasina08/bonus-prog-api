package com.example.demo.repository.model;

import java.util.UUID;
import lombok.Data;

@Data
public class BookItem {
  private UUID id;
  private VolumeInfo volumeInfo;
  private SaleInfo saleInfo;
}
