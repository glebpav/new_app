package com.example.news_app.network.course;

import android.os.AsyncTask;
import android.util.Log;

import com.example.news_app.models.CentBankCurrency;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParseCourse extends AsyncTask<Void, Void, ArrayList<CentBankCurrency>> {

    private final String TAG = "PARSE_COURSE_SPACE";
    private final OnParseCourseListener onParseCourseListener;

    public ParseCourse(OnParseCourseListener onParseCourseListener) {
        this.onParseCourseListener = onParseCourseListener;
    }

    public ArrayList<CentBankCurrency> parse(String xml) {
        try {
            ArrayList<CentBankCurrency> listCurrency = new ArrayList<>();
            CentBankCurrency currency = new CentBankCurrency();

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(new StringReader(xml));

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        String name = myParser.getName();
                        switch (name) {
                            case "Valute":
                                currency = new CentBankCurrency();
                                break;
                            case "NumCode":
                                myParser.next();
                                currency.setNumCode(Integer.valueOf(myParser.getText()));
                                break;
                            case "CharCode":
                                myParser.next();
                                currency.setCharCode(myParser.getText());
                                break;
                            case "Nominal":
                                myParser.next();
                                currency.setNominal(Integer.valueOf(myParser.getText()));
                                break;
                            case "Name":
                                myParser.next();
                                currency.setName(myParser.getText());
                                break;
                            case "Value":
                                myParser.next();
                                currency.setValue(Double.parseDouble(myParser.getText().replace(",", ".")));
                                listCurrency.add(currency);
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = myParser.next();
            }
            for (int i = 0; i < listCurrency.size(); i++) {
                Log.d(TAG, listCurrency.get(i).toString());
            }
            Log.d(TAG, "parse: " + listCurrency);
            return listCurrency;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ArrayList<CentBankCurrency> doInBackground(Void... voids) {
        String responseStr = "";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("https://www.cbr.ru/scripts/XML_daily.asp").get().build();
        try (Response response = client.newCall(request).execute()) {
            responseStr = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "doInBackground: \n" + responseStr);
        return parse(responseStr);
    }

    @Override
    protected void onPostExecute(ArrayList<CentBankCurrency> centBankCurrencies) {
        super.onPostExecute(centBankCurrencies);
        onParseCourseListener.onFound(centBankCurrencies);
    }

    public interface OnParseCourseListener {
        void onFound(ArrayList<CentBankCurrency> listCurrency);
    }
}




