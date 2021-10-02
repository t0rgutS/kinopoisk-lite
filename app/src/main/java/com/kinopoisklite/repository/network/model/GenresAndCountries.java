package com.kinopoisklite.repository.network.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenresAndCountries implements Serializable {
    private List<Genre> genres = new ArrayList<Genre>();
    private List<Country> countries = new ArrayList<Country>();

    /**
     * No args constructor for use in serialization
     */
    public GenresAndCountries() {
    }

    /**
     * @param genres
     * @param countries
     */
    public GenresAndCountries(List<Genre> genres, List<Country> countries) {
        super();
        this.genres = genres;
        this.countries = countries;
    }

    @Getter
    @Setter
    public class Genre implements Serializable {

        private Long id;
        private String genre;

        /**
         * No args constructor for use in serialization
         */
        public Genre() {
        }

        /**
         * @param genre
         * @param id
         */
        public Genre(Long id, String genre) {
            super();
            this.id = id;
            this.genre = genre;
        }

    }

    @Getter
    @Setter
    public class Country implements Serializable {
        private Long id;
        private String country;

        /**
         * No args constructor for use in serialization
         */
        public Country() {
        }

        /**
         * @param country
         * @param id
         */
        public Country(Long id, String country) {
            super();
            this.id = id;
            this.country = country;
        }

    }

}

