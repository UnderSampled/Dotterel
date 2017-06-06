package org.openstenoproject.dotterel;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import org.openstenoproject.dotterel.data.OptimizerTableHelper;
import org.openstenoproject.dotterel.data.DBContract.OptimizationEntry;


public class SuggestionsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.openstenoproject.dotterel.R.layout.activity_suggestions);
        SQLiteDatabase db = new OptimizerTableHelper(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + OptimizationEntry._ID + ", "
                + OptimizationEntry.COLUMN_STROKE + ", "
                + OptimizationEntry.COLUMN_TRANSLATION + ", "
                + OptimizationEntry.COLUMN_OCCURRENCES
                + " FROM " + OptimizationEntry.TABLE_NAME
                + " ORDER BY " + OptimizationEntry.COLUMN_OCCURRENCES + " DESC;", null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                org.openstenoproject.dotterel.R.layout.optimization_item, cursor,
                new String[] {OptimizationEntry.COLUMN_STROKE, OptimizationEntry.COLUMN_TRANSLATION, OptimizationEntry.COLUMN_OCCURRENCES},
                new int[] { org.openstenoproject.dotterel.R.id.stroke, org.openstenoproject.dotterel.R.id.translation, org.openstenoproject.dotterel.R.id.occurrences });
        setListAdapter(adapter);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.suggestions, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
