package com.blecharczyk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ReadServlet extends HttpServlet{
	
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		//https://www.metaweather.com/api/
		
		String location = req.getParameter("location");

		String basicLocationURL = "https://www.metaweather.com/api/location/search/?query=";
		String fullLocationURL = basicLocationURL + location;
        String content = new Scanner(new URL(fullLocationURL).openStream(), "UTF-8").useDelimiter("\\A").next();
        int startIndex = content.indexOf("woeid");
        int stopIndex = content.indexOf(",", startIndex);
        String woeid = content.substring(startIndex + 7, stopIndex);

		//
		PrintWriter out = res.getWriter();
		out.println("Pogoda dla: " + location + " na najblizsze 6 dni:\n\n");
		
		String basicURL = "https://www.metaweather.com/api/location/";
		String fullURL = (basicURL + woeid);
		
		URL url = new URL(fullURL);
		InputStreamReader reader = new InputStreamReader(url.openStream());
		Weather weather = new Gson().fromJson(reader, Weather.class);
		
		System.out.println(weather.toString());
        System.out.println(weather.getConsolidatedWeather().size());
        for (ConsolidatedWeather cw : weather.getConsolidatedWeather()){
            out.println("Data: " + cw.getApplicableDate());
            out.println("Pogoda: " + cw.getWeatherStateName());
            out.println("Temperatura w ciagu dnia: " + cw.getTheTemp());
            out.println("Temperatura maksymalna: " + cw.getMaxTemp());
            out.println("Temperatura minimalna: " + cw.getMinTemp());
            out.println("Prêdkosc wiatru: " + cw.getWindSpeed());
            out.println("Kierunek wiatru: " + cw.getWindDirectionCompass());
            out.println("Cisnienie atmosferyczne: " + cw.getAirPressure());
            out.println("Wilgotnosc: " + cw.getHumidity());
            out.println("\n\n");
           
        }
		
	}
}
