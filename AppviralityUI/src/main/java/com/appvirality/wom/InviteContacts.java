package com.appvirality.wom;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.appvirality.R;

public class InviteContacts extends Activity {

	private LinkedHashMap<Parent,ArrayList<Contact>> groupList, searchList, favContacts;
	private ExpandableListView expandableListView;
	Bitmap defaultImageBitmap;
	private String mSearchTerm;
	private Button btnInvite;
	private int displayRecords = 50, recodsAdded;
	private boolean showMoreList = false, allrecordsdone = false;
	private ProgressBar progressBar;
	private EditText searchBox;
	private Activity activity;
	static LinkedHashMap<Parent,ArrayList<Contact>> allContactsMap, callHashMap;

	int recordsCount;
	boolean status;
	private Button btnSMS, btnEmails;
	private static final String[] CONTACT_PROJECTION = new String[] {
		ContactsContract.Contacts._ID,
		ContactsContract.Contacts.DISPLAY_NAME,
		ContactsContract.Contacts.PHOTO_ID};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appvirality_contacts);
		expandableListView = (ExpandableListView) findViewById(R.id.appvirality_expandableListView);
		btnInvite = (Button) findViewById(R.id.appvirality_btninvitecontacts);
		defaultImageBitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.appvirality_user_image);
		searchBox = (EditText) findViewById(R.id.appvirality_searchbox);
		this.progressBar  = (ProgressBar) findViewById(R.id.appvirality_progress);
		activity = InviteContacts.this;
		btnSMS = (Button) findViewById(R.id.appvirality_btnsms);
		btnEmails = (Button) findViewById(R.id.appvirality_btnmail);
		ExpandableAdapter.setChildLayoutResouces(R.layout.appvirality_group_item, R.id.appvirality_title, R.id.appvirality_cb);
		ExpandableAdapter.setParentLayoutResources(R.layout.appvirality_group_list, R.id.appvirality_title, R.id.appvirality_cb, R.id.appvirality_user_image);
		progressBar.setVisibility(View.VISIBLE);
		GetContacts(true, 1);
		expandableListView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleContact, int visibleContactCount, int totalContactCount) {
				final int lastContact = firstVisibleContact + visibleContactCount;
				if(lastContact == totalContactCount && lastContact != 0) {   				
					if(!allrecordsdone && !showMoreList && TextUtils.isEmpty(mSearchTerm)) {
						showMoreList = true;
						searchBox.setEnabled(false);
						progressBar.setVisibility(View.VISIBLE);
						GetContacts(false, 0);
					}
				}				
			}
		});

		searchBox.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {

			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{            	
				String newFilter = !TextUtils.isEmpty(s) ? String.valueOf(s).trim() : null;
				if (mSearchTerm == null && TextUtils.isEmpty(newFilter)) {
					return;
				}
				else 
					if(!TextUtils.isEmpty(newFilter)) {
						mSearchTerm = newFilter;
						searchContacts();
					}    
					else {
						LinkedHashMap<Parent,ArrayList<Contact>> allContacts = new LinkedHashMap<Parent, ArrayList<Contact>>();
						
						if(favContacts != null && favContacts.size() > 0)
						{
							Parent recentContacts = new Parent();
							recentContacts.name = "Recommended Contacts";

							allContacts.put(recentContacts, new ArrayList<Contact>());
							allContacts.putAll(favContacts);
						}

						Parent AllContacts = new Parent();
						AllContacts.name = "All Contacts";
						allContacts.put(AllContacts, new ArrayList<Contact>());
						
						if(groupList != null)
							allContacts.putAll(groupList);
						ExpandableAdapter contactsAdapter = new ExpandableAdapter(activity, btnInvite, expandableListView, allContacts, favContacts != null ? favContacts.size() : -1, false, btnSMS, btnEmails);
						expandableListView.setAdapter(contactsAdapter);	
					}
				progressBar.setVisibility(View.GONE);
			}
		});

		findViewById(R.id.appvirality_prev).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				allContactsMap = null;
				finish();
			}
		});
		
	}
	
	public void GetContacts(final boolean showrect, final int code)
	{		
		new Thread() {
			@Override
			public void run() {
				if(code == 1 ? allContactsMap == null : true)
					status = initContactList(showrect);

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {		
							searchBox.setEnabled(true);
							if(allContactsMap != null && code == 1)
							{
								ExpandableAdapter contactsAdapter = new ExpandableAdapter(activity, btnInvite, expandableListView, allContactsMap, callHashMap != null ? callHashMap.size() : -1, true, btnSMS, btnEmails);
								expandableListView.setAdapter(contactsAdapter);	
								expandableListView.setVisibility(View.VISIBLE);
								progressBar.setVisibility(View.GONE);
								return;
							}

							LinkedHashMap<Parent,ArrayList<Contact>> allContacts = new LinkedHashMap<Parent, ArrayList<Contact>>();
							if(favContacts != null && favContacts.size() > 0)
							{
								Parent recentContacts = new Parent();
								recentContacts.name = "Recommended Contacts";

								allContacts.put(recentContacts, new ArrayList<Contact>());
								allContacts.putAll(favContacts);
							}

							Parent AllContacts = new Parent();
							AllContacts.name = "All Contacts";
							allContacts.put(AllContacts, new ArrayList<Contact>());
							allContacts.putAll(groupList);
							if(!status)
								return;
							ExpandableAdapter contactsAdapter;
							if(showMoreList)
							{
								showMoreList = false;								
								contactsAdapter = new ExpandableAdapter(activity, btnInvite, expandableListView, allContacts, favContacts != null ? favContacts.size() : -1, false, btnSMS, btnEmails);
								expandableListView.setAdapter(contactsAdapter);	
								expandableListView.setSelectionFromTop(recordsCount - 1, 0);
							}
							else {
								contactsAdapter = new ExpandableAdapter(activity, btnInvite, expandableListView, allContacts, favContacts != null ? favContacts.size() : -1,  showrect, btnSMS, btnEmails);
								expandableListView.setAdapter(contactsAdapter);	
								expandableListView.setVisibility(View.VISIBLE);
								
							}

							recordsCount = allContacts.size();
							allContactsMap = allContacts;
							callHashMap = favContacts;

							progressBar.setVisibility(View.GONE);
						}
						catch(Exception e) {
						}
					}
				});
			}
		}.start();
	}

	private boolean initContactList(boolean showrect)
	{
		try		
		{
			boolean readCallsPermission = true;
			if(android.os.Build.VERSION.SDK_INT > 15) 
				readCallsPermission = checkCallingOrSelfPermission("android.permission.READ_CALL_LOG") == PackageManager.PERMISSION_GRANTED;

			if(showrect && readCallsPermission)
				fetchRecentCalls(defaultImageBitmap);

			ArrayList<Parent> groupsList = fetchGroups(defaultImageBitmap, true);
			if(groupsList == null) {
				allrecordsdone = true;
				return false;
			}

			if(groupList == null || !showMoreList)
				groupList = new LinkedHashMap<Parent, ArrayList<Contact>>();
			for(Parent p : groupsList)
			{
				ArrayList<Contact> groupMembers =new ArrayList<Contact>();
				groupMembers.addAll(fetchGroupMembers(p.cid));
				if(groupMembers.size() > 0)
					groupList.put(p,groupMembers);
			}
		}
		catch(Exception e) 
		{
			return false;
		}
		return true;
	}


	@Override
    public void onBackPressed() {
		super.onBackPressed();
		allContactsMap = null;
	}
	
	public void searchContacts()
	{
		searchList = new LinkedHashMap<Parent, ArrayList<Contact>>();
		ArrayList<Parent> groupsList = fetchGroups(defaultImageBitmap, false);
		for(Parent p : groupsList)
		{
			ArrayList<Contact> groupMembers =new ArrayList<Contact>();
			groupMembers.addAll(fetchGroupMembers(p.cid));
			if(groupMembers.size() > 0)
				searchList.put(p,groupMembers);
		}		

		ExpandableAdapter contactsAdapter = new ExpandableAdapter(activity, btnInvite, expandableListView, searchList, -1, false, btnSMS, btnEmails);
		expandableListView.setAdapter(contactsAdapter);	
	}

	private ArrayList<Parent> fetchGroups(Bitmap defaultImageBitmap, boolean getcontacts)
	{
		ArrayList<Parent> groupList = new ArrayList<Parent>();
		Uri contactsUri;
		if(!TextUtils.isEmpty(mSearchTerm)) {
			contactsUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(mSearchTerm));
		}
		else
		{
			contactsUri = ContactsContract.Contacts.CONTENT_URI;
		}

		String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME
				+ " NOTNULL) AND ("
				+ ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1) AND (" 
				+ ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		int recordstobeadded = recodsAdded + displayRecords;
		Cursor cursor = activity.getContentResolver().query(contactsUri, CONTACT_PROJECTION, selection, null, sortOrder + (TextUtils.isEmpty(mSearchTerm) && getcontacts ? " limit " + recordstobeadded : ""));

		if(TextUtils.isEmpty(mSearchTerm) && showMoreList)
		{
			if(cursor.getCount() > recodsAdded)
				cursor.moveToPosition(recodsAdded - 1);
			else
				return null;

			if(cursor.getCount() >= recordstobeadded)
				allrecordsdone = false;
			else
				allrecordsdone = true;	
		}

		while(cursor.moveToNext()) {
			Parent parent = new Parent();
			parent.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));	
			parent.cid = id;
			
			Bitmap image = getContactProfileImage(cursor.getString(cursor.getColumnIndex("photo_id")));
			parent.image = image == null ? defaultImageBitmap : image;
			groupList.add(parent);
		}
		if(TextUtils.isEmpty(mSearchTerm) && getcontacts)
			recodsAdded = recordstobeadded;

		cursor.close();
		return groupList;
	}

	private LinkedHashMap<Parent, ArrayList<Contact>> fetchRecentCalls(Bitmap defaultImageBitmap)
	{
		favContacts = new LinkedHashMap<Parent,ArrayList<Contact>>();
		ArrayList<String> listOfContacts = new ArrayList<String>();
		Uri queryUri = android.provider.CallLog.Calls.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				CallLog.Calls._ID,
				CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME};
		String sortOrder = String.format("%s " + " limit " + displayRecords + "", CallLog.Calls._ID + " DESC");
		Cursor contacts = activity.getContentResolver().query(queryUri, projection, "(" + CallLog.Calls.CACHED_NAME + " NOT NULL" + ")",null,sortOrder);
		while (contacts.moveToNext()) {
			long id = contacts.getLong(contacts.getColumnIndexOrThrow(Contacts._ID));
			Parent parent = new Parent();

			ArrayList<Contact> list = new ArrayList<Contact>();
			Contact contact = new Contact();
			contact.number = contacts.getString(contacts.getColumnIndex(CallLog.Calls.NUMBER));
			list.add(contact);
			id = fetchContactIdFromPhoneNumber(contact.number);
			parent.cid = id;
			parent.name = contacts.getString(contacts.getColumnIndex(CallLog.Calls.CACHED_NAME));
			InputStream inputStream = Contacts.openContactPhotoInputStream(activity.getContentResolver(),
					ContentUris.withAppendedId(Contacts.CONTENT_URI, id));
			Bitmap image = null;
			if(inputStream!=null)
				image = BitmapFactory.decodeStream(inputStream);
			parent.image = image == null ? defaultImageBitmap : image;


			if(contact.number != null && !listOfContacts.contains(parent.name)) {				
				listOfContacts.add(parent.name);
				ArrayList<Contact> emailId = queryAllEmailAddressesForContact(parent.cid);

				if(emailId != null)
					list.addAll(emailId);
				if(favContacts.size() > 15)
					break;
				favContacts.put(parent, list);
			}
		}
		contacts.close();
		return favContacts;
	}

	private Bitmap getContactProfileImage(String photoId) {
		Bitmap photoBitmap = null;
		if(photoId != null) {
			final Cursor photo = activity.getContentResolver().query(Data.CONTENT_URI,
					new String[] {Photo.PHOTO},					
					Data._ID + "=?",
					new String[]{photoId},
					null);		
			if(photo.moveToFirst()) {
				byte[] photoBlob = photo.getBlob(
						photo.getColumnIndex(Photo.PHOTO));
				photoBitmap = BitmapFactory.decodeByteArray(
						photoBlob, 0, photoBlob.length);
			} else {
				photoBitmap = null;
			}
			photo.close();
		}
		return photoBitmap;
	}

	public Uri getPhotoUri(long contactId) {

		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		return Uri.withAppendedPath(person,ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	public long fetchContactIdFromPhoneNumber(String phoneNumber) {
		try
		{
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));
			Cursor cursor = this.getContentResolver().query(uri,new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },null, null, null);

			long contactId = -1;

			if (cursor.moveToFirst()) {
				contactId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PhoneLookup._ID)));
			}
			cursor.close();

			return contactId;
		}
		catch(Exception e)
		{
			return  -1;
		}
	}

	public ArrayList<Contact> queryAllPhoneNumbersForContact(long contactId) {
		final String[] projection = new String[] {
				Phone.NUMBER,
				Phone.TYPE,
		};

		final Cursor phone = activity.getContentResolver().query(
				Phone.CONTENT_URI,	
				projection,
				Data.CONTACT_ID + "=?",
				new String[]{String.valueOf(contactId)},
				null);

		if(phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);	
			ArrayList<Contact> list = new ArrayList<Contact>();
			while(!phone.isAfterLast()) {
				Contact listHolder = new Contact();
				listHolder.number = phone.getString(contactNumberColumnIndex);
				if(!list.contains(listHolder))
					list.add(listHolder);
				phone.moveToNext();
			}
			phone.close();
			return list;
		}
		phone.close();
		return null;
	}


	public ArrayList<Contact> queryAllEmailAddressesForContact(long contactId) {
		final String[] projection = new String[] {
				Email.DATA,
				Email.TYPE
		};

		final Cursor email = activity.getContentResolver().query(
				Email.CONTENT_URI,	
				projection,
				Data.CONTACT_ID + "=?",
				new String[]{String.valueOf(contactId)},
				null);

		if(email.moveToFirst()) {			
			ArrayList<Contact> list = new ArrayList<Contact>();
			while(!email.isAfterLast()) {
				Contact listHolder = new Contact();
				listHolder.number = email.getString(email.getColumnIndex(Email.DATA));
				listHolder.isEmail = true;
				email.moveToNext();			
				list.add(listHolder);
			}
			email.close();
			return list;
		}
		email.close();
		return null;
	}

	private ArrayList<Contact> fetchGroupMembers(long groupId){
		ArrayList<Contact> groupMembers = new ArrayList<Contact>();
		ArrayList<Contact> phone = queryAllPhoneNumbersForContact(groupId);
		ArrayList<Contact> emailId = queryAllEmailAddressesForContact(groupId);
		if(phone != null)
			groupMembers.addAll(phone);
		if(emailId != null)
			groupMembers.addAll(emailId);

		return groupMembers;
	}
}
