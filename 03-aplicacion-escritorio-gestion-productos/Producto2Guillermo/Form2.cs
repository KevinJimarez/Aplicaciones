using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using ClassExibidorProducts;
using ClaseProducto;
namespace Producto2Guillermo
{
    public partial class Form2 : Form
    {
        Trabajador trabajador;
        int indice;


        public Form2(Trabajador trab, int indi)
        {
            InitializeComponent();
            trabajador = trab;
            indice = indi;
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            label7.Text = trabajador.InfoTrabajador();
            if (trabajador.mostrartodosProductos(indice) != null)
            {
                string[] productos = trabajador.mostrartodosProductos(indice);
                for (int i = 0; i < productos.Length; i++)
                {
                    listBox1.Items.Add(productos[i]);
                }
                textBox6.Text = "";
            }

        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            int i = 0;
            if (int.TryParse(textBox5.Text, out i))
            {
                if (textBox6.Text != "")
                {
                    Producto nuevo = new Producto();
                    nuevo.Codigo = textBox2.Text;
                    nuevo.Descripcion = textBox3.Text;
                    nuevo.Marca = textBox4.Text;
                    nuevo.Precio = Convert.ToInt32(textBox5.Text);
                    nuevo.Foto = Image.FromFile(openFileDialog1.FileName);
                    MessageBox.Show(trabajador.AgregarProductoExhibidor(indice, nuevo));
                    actualizarList();
                }
            }
            else
            {
                MessageBox.Show("Ingrese todos los datos");
            }
        }

        private void button4_Click_1(object sender, EventArgs e)
        {
            DialogResult respuesta;
            respuesta = openFileDialog1.ShowDialog();
            if (respuesta == DialogResult.OK)
            {
                textBox6.Text = openFileDialog1.FileName;
            }
            else
            {
                textBox6.Text = "";
            }
        }

        private void button5_Click_1(object sender, EventArgs e)
        {
            int i = 0;
            if (int.TryParse(textBox5.Text, out i))
            {
                if (textBox6.Text != "")
                {
                    if (int.TryParse(textBox7.Text, out i))
                    {
                        Producto nuevo = new Producto();
                        nuevo.Codigo = textBox2.Text;
                        nuevo.Descripcion = textBox3.Text;
                        nuevo.Marca = textBox4.Text;
                        nuevo.Precio = Convert.ToInt32(textBox5.Text);
                        nuevo.Foto = Image.FromFile(openFileDialog1.FileName);
                        MessageBox.Show(trabajador.AgregarProductoExhibidor(indice, nuevo, Convert.ToInt32(textBox7.Text)));
                        actualizarList();
                    }
                }
            }
            else
            {
                MessageBox.Show("Ingrese todos los datos");
            }
        }

        public void actualizarList()
        {
            listBox1.Items.Clear();
            for (int i = 0; i < trabajador.TotalProductosdeunexhibidor(indice); i++)
            {
                string[] productos = trabajador.mostrartodosProductos(indice);

                listBox1.Items.Add(productos[i]);
            }
        }

        private void button2_Click_1(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex != -1)
            {
                MessageBox.Show(trabajador.EliminarProductoExhibidor(indice, listBox1.SelectedIndex));
                pictureBox1.Image = null;
                actualizarList();
            }
            else
            {
                MessageBox.Show("Debes seleccionar un elemento de la lista");
            }
        }

        private void button3_Click_1(object sender, EventArgs e)
        {
            if (textBox1.Text != "")
            {
                MessageBox.Show(trabajador.EliminarProducto(indice, textBox1.Text));
                pictureBox1.Image = null;
                actualizarList();
            }
            else
            {
                MessageBox.Show("Debes ingresar un ID valido a eliminar");
            }

        }

        private void listBox1_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex != -1)
            {
                pictureBox1.Image = trabajador.Obtenerproducto(indice, listBox1.SelectedIndex).Foto;
            }

        }

       
    }
}
