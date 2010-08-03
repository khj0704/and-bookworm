package com.totsp.bookworm;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.totsp.bookworm.model.Book;
import com.totsp.bookworm.util.DateUtil;
import com.totsp.bookworm.util.StringUtil;


/**
 * Defines the details view screen for a single book.
 * Detail includes the cover image and all book data as well as providing
 * an interface to modify user metadata for the book (eg. ratings, etc).
 */
public class BookDetail extends Activity {

	private static final int MENU_EDIT = 0;
	private static final int MENU_WEB_GOOGLE = 1;
	private static final int MENU_WEB_AMAZON = 2;

	private BookWormApplication application;

	private ImageView bookCover;
	private TextView bookTitle;
	private TextView bookSubTitle;
	private TextView bookAuthors;
	private TextView bookSubject;
	private TextView bookPublisher;
	private TextView bookPubDate;
	private TextView bookFormat;
	private TextView bookTags;
	private TextView bookDescription;

	private RatingBar ratingBar;
	
	private Button selectTagsButton;			// Button to display tag selection dialog
	private ImageButton addTagImage;			// Button to add a new tag to DB
	private AlertDialog.Builder tagDialog;		// Pop-up dialog to select tags linked to current book
	private Cursor tagCursor;
	
	private TextView bookBlurb;   				// Display user blurb
	private Button bookBlurbButton;  			// Edit user blurb button
	private AlertDialog blurbDialog;			// Pop-up dialog to edit user blurb
	EditText blurbEditor;						// Blurb editor dialog view

	private AlertDialog coverDialog;			// Pop-up dialog to display zoomed cover image
	ImageView coverZoomImage;					// ImageView for zooming in on cover image
	boolean coverZoomEnabled;            		// Flag to suppress cover image zoom when no image is available

	private long bookId;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookdetail);
		application = (BookWormApplication) getApplication();

		bookCover = (ImageView) findViewById(R.id.bookcover);
		bookCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (coverZoomEnabled) {
					coverDialog.setTitle(bookTitle.getText());
					coverDialog.show();
				} else {
					// TODO: Consider making this open the cover image manager tab
					Toast.makeText(BookDetail.this, getString(R.string.msgNoImage), Toast.LENGTH_LONG).show();
				}

			}
		});

		bookTitle = (TextView) findViewById(R.id.booktitle);
		bookSubTitle = (TextView) findViewById(R.id.booksubtitle);
		bookAuthors = (TextView) findViewById(R.id.bookauthors);
		bookSubject = (TextView) findViewById(R.id.bookSubject);
		bookPublisher = (TextView) findViewById(R.id.bookPublisher);
		bookPubDate = (TextView) findViewById(R.id.bookPubDate);
		bookFormat = (TextView) findViewById(R.id.bookFormat);
		bookTags = (TextView) findViewById(R.id.bookTags);
		bookDescription = (TextView) findViewById(R.id.bookDescription);


		ratingBar = (RatingBar) findViewById(R.id.bookrating);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(final RatingBar rb, final float val, final boolean b) {
				saveRatingEdit();
			}
		});

		selectTagsButton = (Button) findViewById(R.id.bookSelectTagsButton);
		selectTagsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tagDialog.show();			
			}
		});
		
		addTagImage = (ImageButton) findViewById(R.id.bookAddTagButton);
		addTagImage.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				application.selectedTag = null;
				// TODO: Handle activity result to link new tag since this is presumably the reason it was created.
				startActivity(new Intent(BookDetail.this, TagEditor.class));			
			}
		});


		bookBlurb = (TextView) findViewById(R.id.bookBlurb);
		bookBlurbButton = (Button) findViewById(R.id.bookEditBlurbButton); 
		bookBlurbButton.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) { 
				blurbEditor.setText(bookBlurb.getText());
				blurbDialog.show();
			}   
		});     

		if (application.selectedBook != null) {
			bookId = application.selectedBook.id;
		}

		setupDialogs();
		setViewData();
	}

	
	// go back to Main on back from here
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
			startActivity(new Intent(BookDetail.this, Main.class));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void saveRatingEdit() {
		Book book = application.selectedBook;
		if (book != null) {
			book.bookUserData.rating = (Math.round(ratingBar.getRating()));
			application.dataManager.updateBook(book);
		}
	}


	private void setViewData() {
		Book book = application.selectedBook;
		if (book != null) {
			if (application.debugEnabled) {
				Log.d(Constants.LOG_TAG, "BookDetail book present, will be displayed: " + book.toStringFull());
			}
			Bitmap coverImage = application.imageManager.retrieveBitmap(book.title, book.id, false);
			if (coverImage != null) {
				bookCover.setImageBitmap(coverImage);
				coverZoomImage.setImageBitmap(coverImage);
				coverZoomEnabled = true;
			} else {
				bookCover.setImageResource(R.drawable.book_cover_missing);
				coverZoomEnabled = false;
			}
			bookId = book.id;
			bookTitle.setText(book.title);
			bookSubTitle.setText(book.subTitle);        	 
			if (book.subTitle.trim().contentEquals("")) {
				bookSubTitle.setHeight(0); 
			}

			bookAuthors.setText(StringUtil.contractAuthors(book.authors));
			bookSubject.setText(book.subject);
			if (book.publisher.trim().contentEquals("")) {
				bookPublisher.setText("Unknown Publisher");
			} else {
				bookPublisher.setText(book.publisher);
			}
			bookPubDate.setText(DateUtil.format(new Date(book.datePubStamp)));
			bookFormat.setText(book.format);
			bookDescription.setText(Html.fromHtml(book.description), TextView.BufferType.SPANNABLE);


			ratingBar.setRating(book.bookUserData.rating);
			bookBlurb.setText(book.bookUserData.blurb);
			bookTags.setText(application.dataManager.getBookTagsString(bookId));
		}
	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (application.selectedBook == null) {
			Long id = savedInstanceState.getLong(Constants.BOOK_ID);
			if (id != null) {
				application.establishSelectedBook(id);
				if (application.selectedBook != null) {
					setViewData();
				} else {
					startActivity(new Intent(this, Main.class));
				}
			} else {
				startActivity(new Intent(this, Main.class));
			}
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle saveState) {
		if (application.selectedBook != null) {
			saveState.putLong(Constants.BOOK_ID, application.selectedBook.id);
		}
		super.onSaveInstanceState(saveState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, BookDetail.MENU_EDIT, 0, getString(R.string.menuEdit)).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(0, BookDetail.MENU_WEB_GOOGLE, 1, null).setIcon(R.drawable.google);
		menu.add(0, BookDetail.MENU_WEB_AMAZON, 2, null).setIcon(R.drawable.amazon);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Uri uri = null;
		switch (item.getItemId()) {
		case MENU_EDIT:
			startActivity(new Intent(this, BookForm.class));
			return true;
		case MENU_WEB_GOOGLE:
			// TODO other Locales for GOOG URL?
					uri = Uri.parse("http://books.google.com/books?isbn=" + application.selectedBook.isbn10);
					startActivity(new Intent(Intent.ACTION_VIEW, uri));
					return true;
		case MENU_WEB_AMAZON:
			uri =
				Uri.parse("http://www.amazon.com/gp/search?keywords=" + application.selectedBook.isbn10
						+ "&index=books");
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Creates pop-up dialogs for activity.
	 */
	private void setupDialogs() {
		AlertDialog.Builder blurbBuilder = new AlertDialog.Builder(this);
		AlertDialog.Builder zoomBuilder = new AlertDialog.Builder(this);

		// TODO: Consider extracting tag selection portion to TagDAO to encapsulate knowledge of cursor structure
		tagCursor = application.dataManager.getTagSelectorCursor(bookId);
		tagDialog = new AlertDialog.Builder(this);
		tagDialog.setTitle(getString(R.string.titleTagSelector));
		if ((tagCursor != null) && (tagCursor.getCount() > 0)) {
			startManagingCursor(tagCursor);

			tagDialog.setMultiChoiceItems(tagCursor, new String("tagged"), new String("taggedText"),
					new OnMultiChoiceClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					tagCursor.moveToPosition(which);
					if (application.debugEnabled) {
						Log.v(Constants.LOG_TAG, "Selected Tag: " + tagCursor.getString(1));
					}
					application.dataManager.setBookTagged(bookId, tagCursor.getLong(0), isChecked);
					tagCursor.requery();

					bookTags.setText(application.dataManager.getBookTagsString(bookId));										
				}								
			});		   		   
		}
		else
		{
			tagDialog.setMessage(R.string.msgNoTagsFound);
		}
		tagDialog.create();

		blurbEditor = new EditText(this);
		blurbEditor.setMinLines(3);
		blurbBuilder.setTitle(getString(R.string.titleBlurbEditor))
			.setView(blurbEditor)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						if (bookBlurb.getText() != null) {    
							bookBlurb.setText(blurbEditor.getText());
							Book book = application.selectedBook;    
							book.bookUserData.blurb = bookBlurb.getText().toString();   
							application.dataManager.updateBook(book);
						}
					}
				}
			});
		blurbDialog = blurbBuilder.create();

		coverZoomImage = new ImageView(this);
		// TODO: Check to see if there is a better way to zoom the image.
		// Set image minimum size above maximum to force image to be auto-scaled. 
		coverZoomImage.setMinimumHeight(400);
		coverZoomImage.setMinimumWidth(400);

		zoomBuilder.setView(coverZoomImage);
		
		coverDialog = zoomBuilder.create();
	}
}