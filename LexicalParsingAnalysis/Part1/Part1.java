// Paradeigma ekteleshs
// (((1+2*3)+(3/3-1)+1-1)*2+1-1)/6
// 2.3333333


import java.io.InputStream;
import java.io.IOException;

class Parser {

    private int lookaheadToken;

    private InputStream in;

    public Parser(InputStream in) throws IOException {
	this.in = in;
	lookaheadToken = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
	if (lookaheadToken != symbol)
	    {
	    	{ 
				throw new ParseError();
			}
	    }
	lookaheadToken = in.read();
    }


    public float Factor() throws IOException, ParseError{

    	float result;

    	//periptwsh pou to lookahead einai '('
    	if ( lookaheadToken == '(' )
    	{
    		consume(lookaheadToken);

    		result = Expr();

    		// meta to expr tha prepei ypoxrewtika na akolouthei ')'
    		if (lookaheadToken != ')')
    		{ 
				throw new ParseError();
			}
    		else
    		{
    			consume(lookaheadToken);
    			return result;
    		}
    	}

    	int int_res = Character.getNumericValue(lookaheadToken);
    	result = (float)int_res;

    	// periptwsh pou to lokkahead einai noumero
    	consume(lookaheadToken);

    	return result;

	}

    public float Expr2(float res1) throws IOException, ParseError{


    	if ( lookaheadToken == ')' || lookaheadToken == '\n' || lookaheadToken == -1 ) return res1;
    	if ( lookaheadToken != '+' && lookaheadToken != '-')
    		{ 
				throw new ParseError();
			}

			int token = lookaheadToken;

    	consume(lookaheadToken);

    		float res;

    		res = Term();

    		if (token == '+')
    			res = res + res1;
    		else if (token == '-')
    			res = res1 - res;

    		return Expr2(res);
	}

    public float Expr() throws IOException, ParseError{

    	if ( lookaheadToken != '(' && ( lookaheadToken > '9' || lookaheadToken < '0' ) ) 
			{ 
				throw new ParseError();
			}

			float res = Term();
    		return Expr2(res);

	}

    public float Term2(float res1) throws IOException, ParseError{

    	if ( lookaheadToken == ')' || lookaheadToken == '\n' || lookaheadToken == -1 || lookaheadToken == '+' || lookaheadToken == '-') 
    	{
    		return res1;
    	}
    	if ( lookaheadToken != '*' && lookaheadToken != '/')
    	{ 
			throw new ParseError();
		}

		int token = lookaheadToken;

    	consume(lookaheadToken);

    	float res;

    	res = Factor();

    	if (token == '*')
    		res = res1*res;
    	else if (token == '/')
    		res = res1/res;

    	return Term2(res);
	}


    public float Term() throws IOException, ParseError{

    	if ( lookaheadToken != '(' && ( lookaheadToken > '9' || lookaheadToken < '0' ) ) 
		{ 
			throw new ParseError();
		}

    	float res = Factor();

    	return Term2(res);

	}

	public float Goal() throws IOException, ParseError{

		if ( lookaheadToken != '(' && ( lookaheadToken > '9' || lookaheadToken < '0' ) ) 
		{ 
			throw new ParseError();
		}
		return Expr();
	}

    public float parse() throws IOException, ParseError {
	float goal = Goal();
	if (lookaheadToken != '\n' && lookaheadToken != -1)
	    throw new ParseError();

	return goal;
    }

    public static void main(String[] args) {
	try {
	    Parser parser = new Parser(System.in);
	    float result = parser.parse();
	    System.out.println(result);
	}
	catch (IOException e) {
	    System.err.println(e.getMessage());
	}
	catch(ParseError err){
	    System.err.println(err.getMessage());
	}
    }
}
