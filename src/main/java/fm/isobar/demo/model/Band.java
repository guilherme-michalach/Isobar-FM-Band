package fm.isobar.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Band(String id, String name, String image, String genre, String biography, long numPlays, List<String> albums) {}
