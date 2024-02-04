import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        int number = -1;
        do {
            System.out.println("Выберите действие:");
            System.out.println("1-Довить новый товар");
            System.out.println("2-Вывести все товары");
            System.out.println("0-выход");
            System.out.print("Введите цифру нужного пункта - ");
            try {
                number = enteringNumbers(2);
            } catch (Exception e) {
                System.out.println("Действия с таким номером не существует");
                continue;
            }
            if (number == 1) {
                AddProduct();
            }
            if (number == 2) getNomenclature();

        } while (number != 0);
    }

    private static void AddProduct() throws IOException {
        System.out.print("Введите название товара - ");
        String name = enteringString();
        int price;
        while (true) {
            System.out.print("Введите цену товара - ");
            try {
                price = enteringNumbers();
                break;
            } catch (NumberFormatException ignored) {
                System.out.println("Введено не целое число");
            }
        }
        int department = dataEntry(0);
        int manufacturer = dataEntry(1);
        try (SqLite db = new SqLite()) {
            try {
                db.WriteDBNomenclature(name, price, department, manufacturer);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static int dataEntry(int kodQuery) throws IOException {
        HashMap<Integer, String> spisok = new HashMap<>();
        try (SqLite db = new SqLite()) {
            try {
                ResultSet resultSet = db.ReadDB(kodQuery);
                while (resultSet.next()) {
                    spisok.put(resultSet.getInt("ID"), resultSet.getString("Title"));
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        while (true) {
            System.out.println(switch (kodQuery) {
                case 0 -> "Ввод данных о производителе";
                case 1 -> "Ввод данных об отделе";
                default -> "";
            });
            System.out.println("Выберите действие:");
            for (Map.Entry<Integer, String> element : spisok.entrySet()) {
                System.out.println(element.getKey() + " - " + element.getValue());
            }
            System.out.println("0 - Добавить");
            System.out.print("Введите цифру нужного пункта - ");
            int number;
            try {
                number = enteringNumbers(spisok.size());
            } catch (Exception e) {
                System.out.println("Действия с таким номером не существует");
                continue;
            }
            if (number == 0) {
                addData(kodQuery);
                return dataEntry(kodQuery);
            }
            return number;
        }
    }

    private static void addData(int kodQuery) throws IOException {
        System.out.print(switch (kodQuery) {
            case 0 -> "Введите название производителя - ";
            case 1 -> "Введите название отдела - ";
            default -> "";
        });
        String title = enteringString();
        try (SqLite db = new SqLite()) {
            try {
                db.WriteDB(kodQuery, title);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("Добавить ещё");
            System.out.println("1 - Да");
            System.out.println("0 - Нет");
            System.out.print("Введите цифру нужного пункта - ");
            int number;
            try {
                number = enteringNumbers(1);
            } catch (Exception e) {
                System.out.println("Действия с таким номером не существует");
                continue;
            }
            if (number == 1)
                addData(kodQuery);
            break;
        }
    }

    private static void getNomenclature() throws IOException {
        List<String> Nomenclatures = new ArrayList<>();
        try (SqLite db = new SqLite()) {
            try {
                ResultSet resultSet = db.ReadDBNomenclature();
                while (resultSet.next()) {
                    String Title = "Название - " + resultSet.getString("Title");
                    String Price = "Цена  = " + resultSet.getString("Price");
                    String Department = "Отдел - " + resultSet.getString("Title_department");
                    String Manufacturer = "Производитель - " + resultSet.getString("Title_manufacturer");
                    Nomenclatures.add(Title + " " + Price + " " + Department + " " + Manufacturer);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        while (true){
            System.out.println("Номенклатура Магазина");

            for (String element : Nomenclatures) System.out.println(element);
            System.out.println("Выберите действие:");
            System.out.println("1-Довить новый товар");
            System.out.println("2-назад в меню");
            System.out.println("0-выход");
            System.out.print("Введите цифру нужного пункта - ");
            int number;
            try {
                number = enteringNumbers(2);
            } catch (Exception e) {
                System.out.println("Действия с таким номером не существует");
                continue;
            }
            if (number == 0) System.exit(0);
            if (number == 1){
                AddProduct();
                getNomenclature();
            }
            break;
        }
    }

    private static int enteringNumbers(int max) throws Exception {
        int number = enteringNumbers();
        if (!(number >= 0 && number <= max)) throw new Exception("");
        return number;
    }

    private static String enteringString() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private static int enteringNumbers() throws IOException {
        return Integer.parseInt(enteringString());
    }
}