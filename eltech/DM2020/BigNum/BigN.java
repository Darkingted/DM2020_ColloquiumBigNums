package eltech.DM2020.BigNum;

import java.util.*;

	/**
	* Класс, который позволяет манипулировать с большими натуральными числами + {0}
	* @version 0.04
	* @author Сычев Александр, Яловега Никита, Семенов Алексей
	*/
public class BigN
{
	/*Само число хранится в value - это список. В 0ой ячейке младший разряд, в 1 больше и т.д.
	Например, число 36004256360, в 0ой - 360, в 1ой - 256, во 2ой - 4, в 3ей - 36*/
	private ArrayList<Integer> value = new ArrayList<Integer>();

	private BigN(){}

	/**
	* Конструктор, с помощью которого можно ввести большое натуральное число
	* Если строка src пустая, то в value будет 0 элементов
	*
	* @param String src - представление большого натурального числа в виде строки
	*
	* @version 1.3
	* @author Сычев Александр
	*/
	public BigN(String src) throws IllegalArgumentException
	{
		int n, i;
		src = src.trim();
		n = src.length();
		if (src.charAt(0) == '-')
			throw new IllegalArgumentException("В натуральных числа + {0} не может быть отрицательных");
		if(n % Constants.digits == 1)
		{
			src = "00" + src;
			n+=2;
		}
		if(n % Constants.digits == 2)
		{
			src = "0" + src;
			n++;
		}
		for (i = 0; i < n-3 && Integer.valueOf(src.substring(i, i+3)) == 0; i+=3);
		src = src.substring(i, n);
		n = src.length();
		for(i = 0; i <= n-3; i+=3)
			value.add(Integer.valueOf(src.substring(i, i+3)));
		Collections.reverse(value);
	}

	/**
	* Сложение 2-x больших целых чисел. Вернёт при сложении НОВОЕ большое целое число
	*
    * @param BigN other - число, которое прибавляется к исходному
    * @return BigN result - новое число, получающееся в результате сложения
	*
	* @version 1
	* @author Сычев Александр
	*/
	public BigN add(BigN other)
	{
		BigN buffBigN = new BigN();
		int over, n1, n2, i, j, buff1, buff2, maxCell, n;
		maxCell = 1;
		for(i = 0; i < Constants.digits; i++)
			maxCell *= 10;
		n1 = this.value.size();
		n2 = other.value.size();
		n = Math.max(n1, n2);
		for(i = 0, j = 0, over = 0; i < n || j < n; over = (buff1 + buff2+over)/maxCell, i++, j++)
		{
			if (i < n1)
				buff1 = value.get(i);
			else
				buff1 = 0;
			if(j < n2)
				buff2 = other.value.get(j);
			else
				buff2 = 0;
			buffBigN.value.add((buff1 + buff2 + over) % maxCell);
		}
		if(over != 0)
			buffBigN.value.add(over);
		return buffBigN;
	}

    /**
    *  Разность двух больших натуральных чисел O(this.value.size())
    *  Если вычитаемое больше уменьшаемого, то бросит исключение
	* 
    *  @param BigN other - число, которое вычитаем из исходного
    *  @return BigN result - новое число, получающееся в результате вычитания
    *
    *  @version 0.1
    *  @author Яловега Никита
    */
    public BigN subtract(BigN other) throws ArithmeticException
    {
        int base = 1000;
        int i, j, carry, cur;
        BigN result = this;

        if (this.isMoreOrEquals(other))
        {
            carry = 0;
            for (i = 0; i < other.value.size() || carry != 0; ++i)
            {
                cur = result.value.get(i) - (carry + (i < other.value.size() ? other.value.get(i) : 0));
                carry = cur < 0 ? 1 : 0;
                if (carry == 1)
                    result.value.set(i, cur+base);
                else
                    result.value.set(i, cur);
            }

            for (i = result.value.size()-1; result.value.get(i) == 0 && i > 0; --i)
                result.value.remove(i);
        }
        else
            throw new ArithmeticException("Вычитание невозможно в натуральных числах");
        return result;
    }

	/**
	* Вывод большого натурального числа в виде строки
	* Если в value нуль элементов, то вернёт пустую строку
	*
    * @return Представление числа в виде строки
	*
	* @version 1
	* @author Сычев Александр
	*/
	@Override
	public String toString()
	{
		int i;
		Collections.reverse(value);
		StringBuilder builder = new StringBuilder();
		for(i = 0; i < value.size(); i++)
			if(i != 0)
				builder.append( value.get(i)>=100?value.get(i).toString():(value.get(i)>=10?"0"+value.get(i).toString():"00"+value.get(i).toString()) );
			else
				builder.append(value.get(i).toString());
		Collections.reverse(value);
		return builder.toString();
	}

    /**
     * Умножение двух больших натуральных чисел. O(this.value.size()*other.value.size())
     *
     * @param BigN other - число, на которое нужно умножить исходное
     * @return BigN result - новое число, получающееся в результате умножения
     *
     * @version 0.3333
     * @author Яловега Никита
     */
     public BigN multiply(BigN other)
     {
         int base = 1000;
         BigN result = new BigN();
         int i, j, carry, cur;

         for (i = 0; i < this.value.size() + other.value.size(); ++i)
             result.value.add(0);

        for (i = 0; i < this.value.size(); ++i)
             for (j = 0, carry = 0; j < other.value.size() || carry != 0; ++j)
             {
                 cur = result.value.get(i+j) + this.value.get(i) * (j < other.value.size() ? other.value.get(j) : 0) + carry;
                 result.value.set(i+j, cur % base);
                 carry = cur / base;
             }
			 
        for (i = result.value.size()-1; result.value.get(i) == 0 && i > 0; --i)
     	    result.value.remove(i);
        return result;
 	}

    /**
    * Сравнение двух больших натуральных чисел.
    *
    * @param BigN other - второе число для сравнения с исходным
    * @return int - 0 если равны, -1 если меньше other, 1 если больше other
    *
    * @version 2
    * @author Яловега Никита, Семенов Алексей
    */
    public int compareTo(BigN other)
    {
		String src, compared;
		int buffCompared;
		src = this.toString();
		compared = other.toString();
		if(src.length() > compared.length())
			return 1;
		else if(src.length() < compared.length())
			return -1;
		buffCompared = src.compareTo(compared);
        return buffCompared > 0 ? 1 : (buffCompared < 0 ? -1 : 0 );
    }


    /**
    * @param BigN other
    * @return boolean - true если this больше other, иначе false
    *
    * @version 1
    * @author Яловега Никита
    */
    private boolean isMoreThan(BigN other) {
        return this.compareTo(other) > 0;
    }

    /**
    * @param BigN other
    * @return boolean - true если this меньше other, иначе false
    *
    * @version 1
    * @author Яловега Никита
    */
    private boolean isLessThan(BigN other) {
        return this.compareTo(other) < 0;
    }

    /**
    * @param BigN other
    * @return boolean - true если this больше или равен other, иначе false
    *
    * @version 1
    * @author Яловега Никита
    */
    private boolean isMoreOrEquals(BigN other) {
        return this.compareTo(other) >= 0;
    }

    /**
    * @param BigN other
    * @return boolean - true если this меньше или равен other, иначе false
    *
    * @version 1
    * @author Яловега Никита
    */
    private boolean isLessOrEquals(BigN other) {
        return this.compareTo(other) <= 0;
    }

    /**
    * @param BigN other
    * @return boolean - true если this равен other, иначе false
    *
    * @version 1
    * @author Яловега Никита
    */
    private boolean isEquals(BigN other) {
        return this.compareTo(other) == 0;
    }
	
	/**
	* Сравнение BigN, согласно спецификации Java
	*
    * @return эквивалентность
	*
	* @version 1
	* @author Сычев Александр
	*/
	@Override
    public boolean equals(Object other) 
	{
		if (other == this) return true; 
		if (other == null) return false;
		if( this.getClass() != other.getClass() ) return false;
		return this.isEquals((BigN)other);
    } 


    /**
    * Проверка большого числа на 0.
    *
    * @param BigN num - число для проверки
    * @return boolean - результат проверки
    *
    * @version 1
    * @author Яловега Никита
    */
    public boolean isZero()
    {
        return this.toString().equals("0");
    }
	
	/**
    * Умножение числа на 10^x
    *
    * @param int x - степень
    * @return BigN result - результат умножения
    *
    * @version 1.1
    * @author Семенов Алексей, Сычев Александр, Деменьтев Дмитрий
    */
    public BigN multiplyBy10x(int x)
    {
		String buff = this.toString();;
		if(x < 0) 
		{
			if(x*-1 >= buff.length())
				return new BigN("0");
			buff = buff.substring(0,buff.length()+x);
		}
		else if(x == 0) return this;
		else
		{
			String repeated = "0".repeat(x);
			buff += repeated;
		}
		BigN result = new BigN(buff);
		return result;
    }
    
	/**
	 * вычитание из BigN другого BigN(если получится положительный результат)
	 *
	 * @param BigN other - вычитаемое, BigN k - коофициент домножения other
	 * @return BigN result - результат вычитания из this other*k
	 *
	 * @version 1
	 * @author Кашапова Ольга
	*/
	public BigN subtructByK(BigN other, BigN k) throws ArithmeticException
	{
		if(this.compareTo(other.multiply(k)) == 1 ){
			BigN result = new BigN(this.subtract(other.multiply(k)).toString());
            return result;
		}
		else
			throw new ArithmeticException("Вычитание невозможно в натуральных числах.");
	}
    
	/**
    * инкремент исходного (this) большого натурального числа
    *
    * @return исходное BigN, увеличенное на 1
    *
    * @version 2
    * @author Семенов Алексей, Сычев Александр
    */
    public BigN increment()
    {
		int over, n, i, buff1, buff2;
		boolean f;
		n = this.value.size();
		for (i = 0, f = true; i < n && f ; i++)
		{
			if(this.value.get(i) + 1 >= 1000)
				this.value.set(i, 0);
			else
			{
				this.value.set(i, this.value.get(i) + 1);
				f = false;
			}
		}
		if(f)
			this.value.add(1);
		return this;
    }
	
	/**
    * Деление нацело
    *
    * @param BigN other - делитель
    * @return BigN result - результат деления нацело
    *
    * @version 1.1
    * @author Семенов Алексей, Деменьтев Дмитрий
    */
    public BigN divide(BigN other) throws ArithmeticException
    {
		BigN result = new BigN("0");
		BigN one = new BigN("1");
		BigN buffThis = new BigN(this.toString());
		BigN buffOther = new BigN();
		if(other.isZero()) 
			throw new ArithmeticException("Делить на ноль нельзя!");
		if(this.isLessThan(other)) 
			return result;
		else if(this.isEquals(other)) 
			return result.increment();
		if(other.toString().equals("1"))
			return this;
		Integer diff = this.toString().length()-other.toString().length();
		while(diff >= 0)
		{
			buffOther = other.multiplyBy10x(diff);
			while(buffThis.isMoreOrEquals(buffOther))
			{
				buffThis = buffThis.subtract(buffOther);
				result = result.add(one.multiplyBy10x(diff));
			}
			diff--;
		}
		return result;
    }
    
    /**
    * остаток от деления
    *
    * @param BigN other - делитель
    * @return BigN result - остаток от деления this на other
    *
    * @version 1
    * @author Деменьтев Дмитрий
    */
    public BigN mod(BigN other)
    {
		BigN result = new BigN("0");
		if (this.isLessThan(other)) return this;
        else if (this.equals(other)) return result;
        else
        {
            result = this.subtract(other.multiply(this.divide(other)));
        }
		return result;
    }
    
    /**
    * нод(this;other)
    *
    * @param BigN other - второе число для нахождения нод
    * @return BigN result - нод(this;other)
    *
    * @version 1
    * @author Деменьтев Дмитрий
    */
    public BigN gcd(BigN other)
    {
		BigN buffThis = new BigN(this.toString());
        BigN buffOther = new BigN(other.toString());
		while (!buffThis.isZero() && !buffOther.isZero())
        {
            if (buffThis.isMoreThan(buffOther)) 
                buffThis = buffThis.mod(buffOther);
            else
                buffOther = buffOther.mod(buffThis);
        }
		return buffThis.add(buffOther);
    }
    
    /**
    * нок(this;other)
    *
    * @param BigN other - второе число для нахождения нок
    * @return BigN result - нок(this;other)
    *
    * @version 1
    * @author Деменьтев Дмитрий
    */
    public BigN lcm(BigN other)
    {
		return this.multiply(other).divide(this.gcd(other));
    }
    
 }
