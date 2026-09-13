using ClassExibidorProducts;
namespace Producto2Guillermo
{
    public partial class Form1 : Form
    {
        Trabajador Trabajador = new Trabajador();
        public Form1()
        {
            InitializeComponent();
        }


        private void button1_Click_1(object sender, EventArgs e)
        {
            int i = 0;
            if (int.TryParse(textBox1.Text, out i))
            {
                Exhibidor nuevo = new Exhibidor();
                nuevo.Idexhibidor = Convert.ToInt32(textBox1.Text);
                nuevo.Direccion = textBox2.Text;
                nuevo.Permiso = textBox3.Text;

                MessageBox.Show(Trabajador.AgregarExhibidor(nuevo));
                actualizarList();
            }
            else
            {
                MessageBox.Show("Validar tipos de datos de entrada");
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {



        }

        private void actualizarList()
        {
            listBox1.Items.Clear();
            for (int i = 0; i < Trabajador.TotalExhibidores(); i++)
            {
                listBox1.Items.Add(Trabajador.ObtenerExhibidores()[i]);
            }
        }

        private void button2_Click_1(object sender, EventArgs e)
        {
            MessageBox.Show(Trabajador.EliminarExhibidor(listBox1.SelectedIndex));
            actualizarList();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            if (textBox5.Text != "")
            {
                int i = 0;
                if (int.TryParse(textBox5.Text, out i))
                {


                    MessageBox.Show(Trabajador.EliminarExhibidorID(Convert.ToInt32(textBox5.Text)));
                    actualizarList();
                }
            }
            else
            {
                MessageBox.Show("Debes de ingresar el ID  valido a borrar");
            }
        }

        private void button4_Click_2(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex != -1)
            {
                Form2 form2 = new Form2(Trabajador, listBox1.SelectedIndex);
                form2.ShowDialog();
            }
            else
            {
                MessageBox.Show("Selecciona un exhibidor");
            }
        }













        private void Form1_Load(object sender, EventArgs e)
        {
            
        }

        private void button5_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(textBox4.Text) &&
                !string.IsNullOrEmpty(textBox6.Text) &&
                !string.IsNullOrEmpty(textBox7.Text) &&
                !string.IsNullOrEmpty(textBox8.Text))
            {
                Trabajador.Nombre = textBox4.Text;
                Trabajador.Registro = textBox6.Text;
                Trabajador.ZonaAtiende = textBox7.Text;
                Trabajador.telefono = textBox8.Text;

                label10.Text = Trabajador.InfoTrabajador();

            }
            else
            {
                MessageBox.Show("Todos los campos deben estar llenos.");
            }


        }
    }
}
