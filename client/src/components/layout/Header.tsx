import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { BsPersonCircle } from "react-icons/bs";

const Header = () => {
  return (
    <header className="flex justify-between items-center p-4">
      <h1 className="text-xl font-bold">AI Vision inside</h1>
      <Avatar>
        <AvatarImage src="https://github.com/shadcn.png" alt="User avatar" />
        <AvatarFallback>
          <BsPersonCircle className="w-5 h-5" />
        </AvatarFallback>
      </Avatar>
    </header>
  );
};

export default Header;
