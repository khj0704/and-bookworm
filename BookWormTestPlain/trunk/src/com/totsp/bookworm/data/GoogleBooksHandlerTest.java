package com.totsp.bookworm.data;


import com.totsp.bookworm.data.GoogleBooksHandler;
import com.totsp.bookworm.model.Book;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

// gotta use Junit3 and extend TestCase - to run this in Eclipse without extra config
// remove android lib and add JRE and junit to run config

public class GoogleBooksHandlerTest extends TestCase {

   public void testParse() throws Exception {

      InputStream is = null;
      try {
         GoogleBooksHandler handler = new GoogleBooksHandler();

         File file = new File("res/book_search_response.xml");
         is = new FileInputStream(file);

         XMLReader r = XMLReaderFactory.createXMLReader();
         r.setContentHandler(handler);
         r.parse(new InputSource(is));

         System.out.println("getBooks");
         List<Book> books = handler.getBooks();
         Assert.assertEquals(1, books.size());
         Book book = books.get(0);
         Assert.assertEquals("Unlocking Android", book.getTitle());
         Assert.assertEquals("A Developer's Guide", book.getSubTitle());
         Assert.assertEquals(3, book.getAuthors().size());
         ///System.out.println(book.toStringFull());
      } finally {
         if (is != null) {
            is.close();
         }
      }
   }
   
   public void testParseMult() throws Exception {

      InputStream is = null;
      try {
         GoogleBooksHandler handler = new GoogleBooksHandler();

         File file = new File("res/book_search_mult_response.xml");
         is = new FileInputStream(file);

         XMLReader r = XMLReaderFactory.createXMLReader();
         r.setContentHandler(handler);
         r.parse(new InputSource(is));

         System.out.println("getBooks");
         List<Book> books = handler.getBooks();
         for (Book b : books) {
            ///System.out.println("book - " + b.toString());
         }
        
         Assert.assertEquals(10, books.size());
         Book book = books.get(0);
         Assert.assertEquals("Android", book.getTitle());
         Assert.assertEquals("a programmer's guide", book.getSubTitle());
         Assert.assertEquals(1, book.getAuthors().size());
         System.out.println(book.toStringFull());         
      } finally {
         if (is != null) {
            is.close();
         }
      }
   }
}