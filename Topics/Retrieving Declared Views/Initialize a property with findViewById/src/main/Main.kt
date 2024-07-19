class MainActivity : FakeActivity() {
    lateinit var userNameTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userNameTextView = findViewById(R.id.userNameTextView)
    }
    
}
