package com.example.android.booklisting;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public String URL_RESULT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.search_button);
        final EditText searchBar = (EditText) findViewById((R.id.search_field));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL_RESULT = "https://www.googleapis.com/books/v1/volumes?q=";
                String searchString = searchBar.getText().toString();
                searchString = searchString.trim();
                searchString = searchString.replace(" ", "+");
                URL_RESULT += searchString + "&maxResults=10";
                BooksAsyncTask task = new BooksAsyncTask();
                task.execute();
            }
        });
    }

    private class BooksAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {

            URL url = createUrl(URL_RESULT);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("MainActivity.java", "IO problem");
            }

            ArrayList<Book> books;

            books = extractFeatureFromJson(jsonResponse);

            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }

            BookAdapter bookAdapter = new BookAdapter(MainActivity.this, books);

            ListView bookList = (ListView) findViewById(R.id.book_list);

            bookList.setAdapter(bookAdapter);

            TextView prompt = (TextView) findViewById(R.id.prompt);
            if (prompt.getVisibility() == View.VISIBLE) {
                prompt.setVisibility(View.GONE);
            }
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e("MainActivity.java", "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("MainActivity.java", "The HTTP response code was not 200: " + urlConnection.getResponseCode());
                    return "";
                }
            } catch (IOException e) {
                Log.e("MainActivity.java", "Something went wrong with the IO");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link ArrayList<Book>} by parsing out all of
         * the book items from the input bookJSON string.
         */
        private ArrayList<Book> extractFeatureFromJson(String bookJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                ArrayList<Book> bookList = new ArrayList<Book>();

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject currentItem = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = currentItem.getJSONObject("volumeInfo");
                    String bookTitle = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    // creates a list of the authors as a string
                    String bookAuthors = authors.join(", ") + ".";
                    bookAuthors = bookAuthors.replaceAll("\"", "");
                    bookList.add(new Book(bookTitle, bookAuthors));
                }
                return bookList;

            } catch (JSONException e) {
                Log.e("MainActivity.java", "Problem parsing the book JSON results", e);
            }
            return null;
        }
    }
}
