package com.greenfox.barbara.heroesapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by barba on 30/11/2017.
 */

public interface HeroApi {

        String BASE_URL = "https://simplifiedcoding.net/demos/";

        @GET("marvel")
        Call<List<Hero>> getHeroes();
}

