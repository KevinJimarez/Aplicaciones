using ClaseProducto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClassExibidorProducts
{
    public class Trabajador
    {
        public string Registro;
        public string Nombre;
        public string ZonaAtiende;
        public string telefono;

        private ListaDoblementeLigadaExhibidor ListExhibidores = new ListaDoblementeLigadaExhibidor();
        private int cuentaexhibidores = 0;
        public string AgregarExhibidor(Exhibidor exhibidorNuevo)
        {
            if (ListExhibidores.Inicio == null || ListExhibidores == null)
            {
                ListExhibidores = new ListaDoblementeLigadaExhibidor();
                ListExhibidores.Inicio = new NodoExhibidor();
                ListExhibidores.Inicio.Exhibidor = exhibidorNuevo;
                ListExhibidores.Final = ListExhibidores.Inicio;
                cuentaexhibidores++;
                return "El primer Exhibidor se imserto";
            }
            else
            {
                ListExhibidores.Final.Siguiente = new NodoExhibidor();
                ListExhibidores.Final.Siguiente.Exhibidor = exhibidorNuevo;
                ListExhibidores.Final.Siguiente.Anterior = ListExhibidores.Final;
                ListExhibidores.Final = ListExhibidores.Final.Siguiente;
                cuentaexhibidores++;
                return "Se agrego un Exhibidor nuevo";
            }
        }

        public string EliminarExhibidor(int posicion)
        {
            if (posicion > -1 && posicion <= cuentaexhibidores && cuentaexhibidores > 0)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posicion; i++)
                {
                    temp = temp.Siguiente;
                }


                if (temp == ListExhibidores.Inicio)
                {
                    if (cuentaexhibidores == 1)
                    {
                        ListExhibidores.Inicio = temp.Siguiente;
                        cuentaexhibidores--;
                        return "Se elimino el primer Exhibidor";
                    }
                    ListExhibidores.Inicio = temp.Siguiente;
                    temp.Siguiente.Anterior = null;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino el primer Exhibidor";
                }
                else if (temp == ListExhibidores.Final)
                {
                    ListExhibidores.Final = temp.Anterior;
                    ListExhibidores.Final.Siguiente = null;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino el Exhibidor Final";
                }
                else
                {
                    temp.Anterior.Siguiente = temp.Siguiente;
                    temp.Siguiente.Anterior = temp.Anterior;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino un Exhibidor";
                }
            }
            return "No se encontro el exhibidor";

        }

        public string EliminarExhibidorID(int idexhibidor)
        {
            NodoExhibidor temp = ListExhibidores.Inicio;
            int cont = 1;

            while (temp.Exhibidor.Idexhibidor != idexhibidor && cont <= cuentaexhibidores)
            {
                temp = temp.Siguiente;
                cont++;
            }
            if (cont <= cuentaexhibidores)
            {
                if (temp == ListExhibidores.Inicio)
                {
                    if (cuentaexhibidores == 1)
                    {
                        ListExhibidores.Inicio = temp.Siguiente;
                        cuentaexhibidores--;
                        return "Se elimino el primer Exhibidor";
                    }
                    ListExhibidores.Inicio = temp.Siguiente;
                    temp.Siguiente.Anterior = null;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino el primer Exhibidor";
                }
                else if (temp == ListExhibidores.Final)
                {
                    ListExhibidores.Final = temp.Anterior;
                    ListExhibidores.Final.Siguiente = null;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino el Exhibidor Final";
                }
                else
                {
                    temp.Anterior.Siguiente = temp.Siguiente;
                    temp.Siguiente.Anterior = temp.Anterior;
                    temp = null;
                    cuentaexhibidores--;
                    return "Se elimino un Exhibidor";
                }
            }
            else
            {
                return "No se encontro el Exhibidor";
            }

        }

        public string[] ObtenerExhibidores()
        {
            NodoExhibidor temp = ListExhibidores.Inicio;
            string[] Productos;

            if (cuentaexhibidores != 0)
            {
                Productos = new string[cuentaexhibidores];

                for (int i = 0; i < cuentaexhibidores; i++)
                {
                    Productos[i] = temp.Exhibidor.InfoExhibidor();
                    temp = temp.Siguiente;
                }
                return Productos;
            }
            return null;
        }

        public int TotalExhibidores()
        {
            return cuentaexhibidores;
        }

        public string InfoTrabajador()
        {
            return "Registro:" + Registro + ", Nombre:" + Nombre + ",ZonaAtiende:" + ZonaAtiende + ", Telefono:" + telefono;
        }

        public string AgregarProductoExhibidor(int posiexhibi, Producto productoNuevo)
        {
            if (productoNuevo != null && posiexhibi != -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }


                return temp.Exhibidor.AgregarProducto(productoNuevo);
            }
            return "No se logro insetar el producto";
        }
        public string AgregarProductoExhibidor(int posiexhibi, Producto productoNuevo, int totalpiezas)
        {
            if (productoNuevo != null && posiexhibi != -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;
                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }

                temp.Exhibidor.AgregarProducto(productoNuevo, totalpiezas);
                return "Se agrego un nuevo producto con numero de piezas";
            }
            return "No se inserto el producto";
        }



        public string EliminarProducto(int posiexhibi, string poridprod)
        {
            if (posiexhibi > -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;
                int j;
                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }

                for (j = 0; j < temp.Exhibidor.TotalProductos(); j++)
                {
                    if (temp.Exhibidor.Obtenerproducto(j) == null)
                    {
                        return "No se encontro el producto";
                    }

                }
                if (j <= temp.Exhibidor.TotalProductos())
                {

                    temp.Exhibidor.EliminarProducto(poridprod);
                    return "Se Elimino el producto por iD";
                }
            }
            return "No se elimino el producto";
        }
        public string EliminarProductoExhibidor(int posiexhibi, int posicionprod)
        {
            if (posiexhibi > -1 || posicionprod > -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }

                temp.Exhibidor.EliminarProducto(posicionprod);
                return "Se elimino  producto del exhibidor y su total de piezas";
            }
            return "No se elimino el producto";
        }
        public string[] mostrartodosProductos(int posiexhibi)
        {
            if (posiexhibi > -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }


                return temp.Exhibidor.mostrartodosProductos();
            }
            return null;
        }
        public int TotalProductosdeunexhibidor(int posiexhibi)
        {
            if (posiexhibi > -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }


                return temp.Exhibidor.TotalProductos();
            }
            return -1;
        }

        public Producto Obtenerproducto(int posiexhibi, int posicionprod)
        {
            if (posiexhibi > -1 || posicionprod > -1)
            {
                NodoExhibidor temp = ListExhibidores.Inicio;

                for (int i = 0; i < posiexhibi; i++)
                {
                    temp = temp.Siguiente;
                }


                return temp.Exhibidor.Obtenerproducto(posicionprod);
            }
            return null;
        }






    }
}
