package currit.net.tonality;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import currit.net.tonality.databinding.FragmentPianoControlKeysBinding;


public class PianoControlKeys extends PianoControlFragment {
    private FragmentPianoControlKeysBinding binding;

    public PianoControlKeys() {
        // Required empty public constructor
    }

    @Override
    public void setPiano(TonalityPianoView piano) {
        super.setPiano(piano);
        binding.setPiano(piano);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_piano_control_keys, container, false);
        return binding.getRoot();
    }
}