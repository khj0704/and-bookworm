package com.totsp.bookworm.data;

import android.os.Environment;

public class DataConstants {

   private static final String APP_PACKAGE_NAME = "com.totsp.bookworm";
   private static final String EXTERNAL_DATA_DIR_NAME = "bookwormdata";
   public static final String DATABASE_NAME = "bookworm.db";
   public static final String DATABASE_PATH =
            Environment.getDataDirectory() + "/data/" + DataConstants.APP_PACKAGE_NAME + "/databases/"
                     + DataConstants.DATABASE_NAME;
   public static final String EXTERNAL_DATA_PATH =
            Environment.getExternalStorageDirectory() + "/" + DataConstants.EXTERNAL_DATA_DIR_NAME;

   public static final String ORDER_BY_AUTHORS_ASC = "authors collate nocase asc, book.tit collate nocase asc";
   public static final String ORDER_BY_AUTHORS_DESC = "authors collate nocase desc, book.tit collate nocase asc";
   public static final String ORDER_BY_TITLE_ASC = "book.tit collate nocase asc";
   public static final String ORDER_BY_TITLE_DESC = "book.tit collate nocase desc";
   public static final String ORDER_BY_SUBJECT_ASC = "book.subject collate nocase asc, book.tit collate nocase asc";
   public static final String ORDER_BY_SUBJECT_DESC = "book.subject collate nocase desc, book.tit collate nocase asc";
   public static final String ORDER_BY_RATING_ASC = "bookuserdata.rat asc, book.tit collate nocase asc";
   public static final String ORDER_BY_RATING_DESC = "bookuserdata.rat desc, book.tit collate nocase asc";
   public static final String ORDER_BY_PUB_ASC = "book.pub collate nocase asc, book.tit collate nocase asc";
   public static final String ORDER_BY_PUB_DESC = "book.pub collate nocase desc, book.tit collate nocase asc";
   public static final String ORDER_BY_DATE_PUB_ASC = "book.datepub asc, book.tit collate nocase asc";
   public static final String ORDER_BY_DATE_PUB_DESC = "book.datepub desc, book.tit collate nocase asc";
   public static final String ORDER_BY_TAG_TEXT_ASC = "tags.ttext collate nocase asc";
   public static final String ORDER_BY_TAG_POSITION_ASC = "(select tagbooks.tbid from tagbooks "
	   													+ "where (tagbooks.tid=%d and tagbooks.bid=book.bid)) asc";
   
   // Filters are designed to work with String.format to allow complex filter criteria (eg authors)
   // SQLite "like" operator is used instead of "glob" to allow configurable control of case-sensitive searches
   public static final String FILTER_BY_AUTHOR = "where book.bid in (select bookauthor.bid from bookauthor " 
	                                                           + "join author on (bookauthor.aid=author.aid) " 
	                                                           + "where author.name like '%%%s%%')";
   public static final String FILTER_BY_TITLE = "where book.tit like '%%%s%%'";
   public static final String FILTER_BY_SUBJECT = "where book.subject like '%%%s%%'";
   public static final String FILTER_BY_PUBLISHER = "where book.pub like '%%%s%%'";
   public static final String FILTER_BY_TAG = "where book.bid in (select tagbooks.bid from tagbooks " 
	   												+ "join tags on (tags.tid=tagbooks.tid) " 
	   												+ "where tags.ttext like '%%%s%%')";
   public static final String FILTER_BY_CURRENT_TAG = "where book.bid in (select tagbooks.bid from tagbooks " 
															+ "where tagbooks.tid=%d)";
   public static final String FILTER_BY_RATING = "where bookuserdata.rat=%s";

   public static final String BOOK_TABLE = "book";
   public static final String BOOKUSERDATA_TABLE = "bookuserdata";
   public static final String BOOKAUTHOR_TABLE = "bookauthor";
   public static final String AUTHOR_TABLE = "author";
   public static final String TAG_TABLE = "tags";
   public static final String TAG_BOOKS_TABLE = "tagbooks";

   public static final String BOOKID = "bid";
   public static final String BOOKUSERDATAID = "budid";
   public static final String BOOKAUTHORID = "baid";
   public static final String BOOKLISTID = "blid";
   public static final String AUTHORID = "aid";
   public static final String TAG_ID = "tid";
   public static final String TAG_BOOK_ID = "tbid";

   public static final String ISBN10 = "isbn10";
   public static final String ISBN13 = "isbn13";
   public static final String TITLE = "tit";
   public static final String SUBTITLE = "subtit";
   public static final String DATEPUB = "datepub";
   public static final String NAME = "name";
   public static final String RATING = "rat";
   public static final String READSTATUS = "rstat";
   public static final String BLURB = "blurb";
   public static final String DESCRIPTION = "desc";
   public static final String PUBLISHER = "pub";
   public static final String FORMAT = "format";
   public static final String SUBJECT = "subject";
   public static final String TAGTEXT = "ttext";
    


   private DataConstants() {
   }
}