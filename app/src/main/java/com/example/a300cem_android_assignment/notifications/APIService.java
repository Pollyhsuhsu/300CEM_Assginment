package com.example.a300cem_android_assignment.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAT1zOwvQ:APA91bFToM411KdcLnQAlZvtFAndEvJUxFvx05DHRwWDue6y8zZ_8ISRxkfzILEIOW6ki8zuohHGwcdcj3ACl8kKm4OKG0SAl00exqHNm-AU3l4b5dJn09menO4JSLopvTQaJO_wrZ1w"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
