package com.razat.moviecatalogservice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.razat.moviecatalogservice.models.CatalogItem;
import com.razat.moviecatalogservice.models.Movie;
import com.razat.moviecatalogservice.models.Rating;
import com.razat.moviecatalogservice.models.UserRating;
import com.razat.moviecatalogservice.services.MovieInfo;
import com.razat.moviecatalogservice.services.UserRatingInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;
    
    @Autowired
    UserRatingInfo userRatingInfo;
    
    @Autowired
    MovieInfo movieInfo;
    
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating userRating = userRatingInfo.getUserRating(userId);
        // using hystrix circut breaker
        //restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);

        return userRating.getRatings().stream()
                .map(rating -> {
                    /* Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                    return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating()); */
                	// using hystrix circut breaker
                	return movieInfo.getCatalogItem(rating);
                })
                .collect(Collectors.toList());

    }
}

/*
Alternative WebClient way
Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movies/"+ rating.getMovieId())
.retrieve().bodyToMono(Movie.class).block();
*/