import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import CurrentMemberContext from "@/contexts/CurrentMemberContext";
import { use } from "react";
import { BsPersonCircle } from "react-icons/bs";

function Header() {
  const { currentMember } = use(CurrentMemberContext);

  return (
    <header className="flex justify-between items-center p-4">
      <h1 className="text-xl font-bold">AI Vision inside</h1>
      <Avatar>
        <AvatarImage src={currentMember?.avatar} alt="User avatar" />
        <AvatarFallback>
          <BsPersonCircle className="w-5 h-5" />
        </AvatarFallback>
      </Avatar>
    </header>
  );
}

export default Header;
