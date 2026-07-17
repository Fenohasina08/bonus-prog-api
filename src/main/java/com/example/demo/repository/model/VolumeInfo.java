package com.example.demo.repository.model;

import java.util.List;
import lombok.Data;

@Data
public class VolumeInfo {
  private String title;
  private List<String> authors;
  private String description;
  private List<IndustryIdentifier> industryIdentifiers;
  private ImageLinks imageLinks;
}
