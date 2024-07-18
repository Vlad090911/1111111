package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchfloorBooksLibrary {
  public static void main(String[] args) {
    while (true) {
      try {
        runTask();

        // Затримка на 24 години (24 години * 60 хвилин * 60 секунд * 1000 мілісекунд)
        Thread.sleep(24 * 60 * 60 * 1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  private static void runTask() {
    ArrayList<String> authors = new ArrayList<>();
    ArrayList<String> series = new ArrayList<>();
    ArrayList<String> bookTitles = new ArrayList<>();
    ArrayList<String> bookNumbers1 = new ArrayList<>();
    ArrayList<String> downloadButtonXPaths = new ArrayList<>();
    ArrayList<String> idbook = new ArrayList<>();
    ArrayList<String> idbookfound = new ArrayList<>();
    Set<String> existingIds = new HashSet<>();

    try {
      String url = "https://searchfloor.org/?status=is_finished";
      Document document = Jsoup.connect(url).get();
      if (document != null) {
        Elements elements = document.select("div[id^=book][style=\"margin-top: 1rem;\"]");
        for (Element element : elements) {
          String idbookfound1 = element.attr("id").substring(4);
          idbookfound.add(idbookfound1);
        }

        try (BufferedReader br = new BufferedReader(new FileReader("idbook.txt"))) {
          String line;
          while ((line = br.readLine()) != null) {
            existingIds.add(line.trim());
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        try (PrintWriter writer2 = new PrintWriter(new FileWriter("idbookfound.txt", true))) {
          for (String id : idbookfound) {
            writer2.println(id);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        try (BufferedReader br1 = new BufferedReader(new FileReader("idbookfound.txt"));
             PrintWriter writer1 = new PrintWriter(new FileWriter("idbook.txt", true));
             PrintWriter writer = new PrintWriter(new FileWriter("library.txt", true))) {

          String line1;
          while ((line1 = br1.readLine()) != null) {
            if (!existingIds.contains(line1.trim())) {
              Element NumberElement3 = document.selectFirst("div[id=\"book" + line1 + "\"][style=\"margin-top: 1rem;\"]");
              if (NumberElement3 != null) {
                Elements bookElements = document.select("div[id=\"book" + line1 + "\"][style=\"margin-top: 1rem;\"]");
                bookElements.forEach(bookElement -> {
                  Element authorsElement1 = bookElement.child(1);
                  Element titleElement1 = bookElement.child(0);
                  Element seriesElement1 = bookElement.child(2);
                  Element seriesElement2 = seriesElement1.selectFirst("a");


                  String author = authorsElement1.text().replace("Автор: ", "");
                  String title = titleElement1.selectFirst("b").text();

                  authors.add(author);
                  bookTitles.add(title);

                  if (seriesElement1 != null && seriesElement2 != null) {
                    String serie = seriesElement2.text();
                    String bookNumber2 = bookElements.last().child(2).text();
                    char bookNumber = bookNumber2.charAt(bookNumber2.length() - 1);
                    series.add(serie);
                    bookNumbers1.add(String.valueOf(bookNumber));
                  } else {
                    series.add("");
                    bookNumbers1.add("");
                  }

                  System.out.println(author);
                });

                idbook.add(line1);
                String downloadButtonXPath = "//*[@id=\"" + line1 + "\"]";
                downloadButtonXPaths.add(downloadButtonXPath);
                removeFirstLineFromFile("idbookfound.txt");


              }
            }
          }

          for (String id : idbook) {
            writer1.println(id);
          }

          for (int j = 0; j < authors.size(); j++) {
            writer.println(authors.get(j));
            writer.println(series.get(j));
            writer.println(bookTitles.get(j));
            writer.println(bookNumbers1.get(j));
            writer.println(downloadButtonXPaths.get(j));
            writer.println();
          }

        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private static void removeFirstLineFromFile(String filePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    if (!lines.isEmpty()) {
      lines.remove(0);
      Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
    }
  }
}


