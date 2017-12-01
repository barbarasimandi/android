package com.greenfox.barbara.fumberz;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by barba on 29/11/2017.
 */

public interface Api {

    String BASE_URL = "http://numbersapi.com/random/";

    @GET("math?json")
    Call<NumberFact> getNumberFact();

    @GET("year?json")
    Call<YearFact> getYearFact();

}
