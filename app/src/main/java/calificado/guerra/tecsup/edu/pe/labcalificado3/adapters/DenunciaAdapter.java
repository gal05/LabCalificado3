package calificado.guerra.tecsup.edu.pe.labcalificado3.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;
import calificado.guerra.tecsup.edu.pe.labcalificado3.activities.Bienvenido;
import calificado.guerra.tecsup.edu.pe.labcalificado3.models.Denuncia;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiService;

public class DenunciaAdapter extends RecyclerView.Adapter<DenunciaAdapter.VievHolder>{

    private List<Denuncia> denuncias;

    public DenunciaAdapter() {
        this.denuncias = new ArrayList<>();
    }

    public void setDenuncias(List<Denuncia> denuncias) {
        this.denuncias = denuncias;
    }

    public class VievHolder extends RecyclerView.ViewHolder {

        public TextView tituloText;
        public TextView propietarioText;
        public ImageView photo;


        public VievHolder(View itemView) {
            super(itemView);
            tituloText=itemView.findViewById(R.id.titulo_text);
            propietarioText=itemView.findViewById(R.id.autor_text);
            photo=itemView.findViewById(R.id.foto_image);


        }
    }


    @Override
    public DenunciaAdapter.VievHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_denuncia, parent, false);
        return new VievHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DenunciaAdapter.VievHolder viewHolder, int position) {


        Denuncia denuncia= this.denuncias.get(position);

        viewHolder.tituloText.setText(denuncia.getTitulo());
        viewHolder.propietarioText.setText("Por : " + Bienvenido.The_usuario);

        String url = ApiService.API_BASE_URL + "/images/" + denuncia.getFoto();
        Picasso.with(viewHolder.itemView.getContext()).load(url).into(viewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return this.denuncias.size();
    }



}
