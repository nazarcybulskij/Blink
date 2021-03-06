package com.pc.maskedtext;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import static android.text.TextUtils.isDigitsOnly;
import static android.text.TextUtils.isEmpty;
import static android.widget.TextView.BufferType.EDITABLE;

/**
 * Created by Pietro Caselani
 * On 1/30/15
 * library
 */
public final class MaskedWatcher implements TextWatcher {
	//region Fields
	private final EditText mEditText;
	private final String mMask;
	private boolean mIsUpdating, mAcceptOnlyNumbers;
	private char mCharRepresentation;
	//endregion

	//region Constructors
	public MaskedWatcher(EditText editText, String mask) {
		if (mask == null) throw new RuntimeException("Mask can't be null");
		if (editText == null) throw new RuntimeException("EditText can't be null");

		mEditText = editText;
		mMask = mask;
		mIsUpdating = false;
		editText.addTextChangedListener(this);
		mCharRepresentation = '#';

		if (editText.getEditableText() == null) {
			editText.setTextKeepState("", EDITABLE);
		}
	}
	//endregion

	//region Getters and Setters
	public boolean acceptOnlyNumbers() {
		return mAcceptOnlyNumbers;
	}

	public void setAcceptOnlyNumbers(boolean acceptOnlyNumbers) {
		mAcceptOnlyNumbers = acceptOnlyNumbers;
	}

	public char getCharRepresentation() {
		return mCharRepresentation;
	}

	public void setCharRepresentation(char charRepresentation) {
		mCharRepresentation = charRepresentation;
	}
	//endregion

	//region Text Watcher
	@Override public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
		if (mIsUpdating) {
			mIsUpdating = false;
			return;
		}

		final CharSequence insertedSequence = charSequence.subSequence(start, count + start);

		if (mAcceptOnlyNumbers && !isNumeric(insertedSequence)) {
			delete(start, start + count);
			return;
		}

		if (charSequence.length() > mMask.length()) {
			delete(start, start + count);
			return;
		}

		final int length = mEditText.length();

		for (int i = 0; i < length; i++) {
			final char m = mMask.charAt(i);
			final char e = mEditText.getText().charAt(i);
			if (m != mCharRepresentation && m != e) {
				mIsUpdating = true;
				mEditText.getEditableText().insert(i, String.valueOf(m));
			}
		}
	}

	@Override public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

	@Override public void afterTextChanged(Editable editable) {}
	//endregion

	//region Private
	private void delete(int start, int end) {
		mIsUpdating = true;
		//TODO setText change the keyboard
		mEditText.setTextKeepState(mEditText.getText().delete(start, end), EDITABLE);
	}

	private boolean isNumeric(CharSequence charSequence) {
		return isEmpty(charSequence) || isDigitsOnly(charSequence);
	}
	//endregion
}
