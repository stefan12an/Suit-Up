package com.example.foodiezapp.main.data.network

class YelpApiInterface {

    @GET("volley_array.json")
    fun getMovies() : Call<List<Movie>>

    companion object {

        var BASE_URL = "http://velmm.com/apis/"

        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)

        }
    }
}