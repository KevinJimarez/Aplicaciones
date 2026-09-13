using ClaseProducto;
namespace ClassExibidorProducts
{
    public class Exhibidor
    {
        public int Idexhibidor;
        public string Direccion;
        public string Permiso;

        private ListaLigadaProducts ListProducts = new ListaLigadaProducts();

        public string AgregarProducto(Producto NuevoProduct, int totalPiezas)
        {

            if (ListProducts.Inicio == null)
            {
                ListProducts.Inicio = new NodoProducto();
                ListProducts.Inicio.producto = new Producto();
                ListProducts.Inicio.numPiezas = totalPiezas;
                ListProducts.Inicio.producto = NuevoProduct;
                ListProducts.Final = ListProducts.Inicio;
                ListProducts.Inicio.numPiezas = totalPiezas;
                ListProducts.ContaNodos++;
                return "Se inserto el Inicio correctamente";
            }
            else
            {
                ListProducts.Final.siguiente = new NodoProducto();
                ListProducts.Final.siguiente.producto = new Producto();
                ListProducts.Final.siguiente.producto = NuevoProduct;
                ListProducts.Final.siguiente.numPiezas = totalPiezas;
                ListProducts.Final = ListProducts.Final.siguiente;
                ListProducts.ContaNodos++;
                return "Se agrego un nodo correctamente";
            }
        }
        public string AgregarProducto(Producto NuevoProduct)
        {
            if (ListProducts.Inicio == null)
            {
                ListProducts.Inicio = new NodoProducto();
                ListProducts.Inicio.producto = new Producto();
                ListProducts.Inicio.producto = NuevoProduct;
                ListProducts.Final = ListProducts.Inicio;
                ListProducts.ContaNodos++;
                return "Se inserto el Inicio correctamente";
            }
            else
            {
                ListProducts.Final.siguiente = new NodoProducto();
                ListProducts.Final.siguiente.producto = new Producto();
                ListProducts.Final.siguiente.producto = NuevoProduct;
                ListProducts.Final = ListProducts.Final.siguiente;
                ListProducts.ContaNodos++;
                return "Se agrego un nodo correctamente";
            }

        }

        public string EliminarProducto(int posicion)
        {
            NodoProducto temp1 = ListProducts.Inicio;
            NodoProducto temp2 = ListProducts.Inicio;

            for (int i = 0; i < posicion; i++)
            {

                if (temp1 == temp2)
                {
                    temp2 = temp2.siguiente;
                }
                else
                {
                    temp1 = temp1.siguiente;
                    temp2 = temp1.siguiente;
                }
            }

            if (temp2 == ListProducts.Inicio)
            {
                ListProducts.Inicio = temp2.siguiente;
                temp1 = null;
                ListProducts.ContaNodos--;
                return "Se elimino el primer nodo";
            }
            else if (temp2 == ListProducts.Final)
            {
                ListProducts.Final = temp1;
                ListProducts.Final.siguiente = null;
                temp2 = null;
                ListProducts.ContaNodos--;
                return "Se elimino el nodo Final";
            }
            else
            {
                temp1.siguiente = temp2.siguiente;
                temp2 = null;
                ListProducts.ContaNodos--;
                return "Se elimino un nodo Intermedio";
            }

        }

        public string EliminarProducto(string codigo)
        {
            NodoProducto temp1 = ListProducts.Inicio;
            NodoProducto temp2 = ListProducts.Inicio;

            while (temp2.producto.Codigo != codigo)
            {

                if (temp1 == temp2)
                {
                    temp2 = temp2.siguiente;
                }
                else
                {
                    temp1 = temp1.siguiente;
                    temp2 = temp1.siguiente;
                }
            }


            if (temp2 == ListProducts.Inicio)
            {
                ListProducts.Inicio = temp2.siguiente;
                temp1 = null;
                ListProducts.ContaNodos--;
                return "Se elimino el primer nodo";
            }
            else if (temp2 == ListProducts.Final)
            {
                ListProducts.Final = temp1;
                ListProducts.Final.siguiente = null;
                temp2 = null;
                ListProducts.ContaNodos--;
                return "Se elimino el nodo Final";
            }
            else
            {
                temp1.siguiente = temp2.siguiente;
                temp2 = null;
                ListProducts.ContaNodos--;
                return "Se elimino un nodo Intermedio";
            }
        }

        public string[] mostrartodosProductos()
        {

            string[] Productos;
            if (ListProducts != null)
            {
                if (ListProducts.ContaNodos != 0)
                {
                    NodoProducto temp = ListProducts.Inicio;
                    Productos = new string[ListProducts.ContaNodos];

                    for (int i = 0; i < ListProducts.ContaNodos; i++)
                    {
                        Productos[i] = temp.producto.InfoProducto() + " Total productos:" + temp.numPiezas;
                        temp = temp.siguiente;
                    }
                    return Productos;
                }
            }
            return null;
        }
        public int TotalProductos()
        {
            return ListProducts.ContaNodos;
        }

        public string InfoExhibidor()
        {
            return "ID:" + Idexhibidor + ", Direccion:" + Direccion + ", Permiso:" + Permiso;
        }

        public Producto Obtenerproducto(int posicion)
        {
            NodoProducto temp = ListProducts.Inicio;

            for (int i = 0; i < posicion; i++)
            {
                temp = temp.siguiente;
            }

            return temp.producto;
        }







    }
}
