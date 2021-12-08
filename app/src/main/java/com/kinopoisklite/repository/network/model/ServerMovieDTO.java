package com.kinopoisklite.repository.network.model;

import com.kinopoisklite.model.Movie;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerMovieDTO extends Movie implements Serializable {
    private String ratingCategory;
    private ServerCoverDTO cover;
}