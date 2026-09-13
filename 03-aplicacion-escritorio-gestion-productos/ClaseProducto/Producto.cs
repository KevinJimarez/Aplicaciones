using System.Drawing;

namespace ClaseProducto

{
    public class Producto
    {
        public string Codigo { get; set; }
        public string Descripcion { get; set; }
        public string Marca { get; set; }
        public float Precio { get; set; }
        public Image Foto { get; set; }


        public string InfoProducto()
        {
            return "codigo:" + Codigo + ", Descripcion:" + Descripcion + ", Marca:" + Marca + ", Precio:" + Precio;
        }


    }
}
