package com.abhishek.abc.notes.network;

import com.abhishek.abc.notes.network.models.NotesModel;
import com.abhishek.abc.notes.network.models.UserModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NetworkInterface {
    // Register new user
    @FormUrlEncoded
    @POST("notes/user/register")
    Single<UserModel> register(@Field("device_id") String deviceId);

    // Create note
    @FormUrlEncoded
    @POST("notes/new")
    Single<NotesModel> createNote(@Field("note") String note);

    // Fetch all notes
    @GET("notes/all")
    Single<List<NotesModel>> fetchAllNotes();

    // Update single note
    @FormUrlEncoded
    @PUT("notes/{id}")
    Completable updateNote(@Path("id") int noteId, @Field("note") String note);

    // Delete note
    @DELETE("notes/{id}")
    Completable deleteNote(@Path("id") int noteId);
}
