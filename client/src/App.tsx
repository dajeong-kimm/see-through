import Header from "@/components/layout/Header";
import MainPage from "@/pages/MainPage";
import CurrentMemberProvider from "@/providers/CurrentMemberProvider";

function App() {
  return (
    <div className="max-w-md mx-auto bg-white min-h-screen">
      <CurrentMemberProvider>
        <Header />
        <MainPage />
      </CurrentMemberProvider>
    </div>
  );
}

export default App;
