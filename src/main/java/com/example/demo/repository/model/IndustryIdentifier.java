package com.example.demo.repository.model;

import lombok.Data;

@Data
public class IndustryIdentifier {
  private String type; // ex: ISBN_13
  private String identifier;
}
